package com.bookeyproject.bookey.handler

import com.bookeyproject.bookey.client.OpenGraphClient
import com.bookeyproject.bookey.domain.BookmarkRequest
import com.bookeyproject.bookey.repository.BookmarkRepository
import com.bookeyproject.bookey.service.BookmarkTransformer
import com.bookeyproject.bookey.service.OpenGraphService
import kotlinx.coroutines.flow.map
import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Service
class BookmarkHandler(
    private val bookmarkRepository: BookmarkRepository,
    private val bookmarkTransformer: BookmarkTransformer,
    private val openGraphService: OpenGraphService
) {
    private val log = KotlinLogging.logger { }

    suspend fun getBookmarks(request: ServerRequest): ServerResponse =
        request.attributeOrNull("userId")
            ?.let { bookmarkRepository.findAllByOwnerId(it as String) }
            ?.map { bookmarkTransformer.fromModel(it) }
            ?.let { ok().contentType(MediaType.APPLICATION_JSON).bodyAndAwait(it) }
            ?: notFound().buildAndAwait()

    suspend fun getBookmark(request: ServerRequest): ServerResponse =
        bookmarkRepository.findById(request.pathVariable("id"))
            ?.let { bookmarkTransformer.fromModel(it) }
            ?.let { ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(it) }
            ?: notFound().buildAndAwait()

    suspend fun getOpenGraphInfo(request: ServerRequest): ServerResponse =
        request.queryParamOrNull("url")
            ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }
            ?.let { openGraphService.getOpenGraphInfo(it) }
            ?.let { ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(it) }
            ?: notFound().buildAndAwait()

    suspend fun addBookmark(request: ServerRequest): ServerResponse =
        request.awaitBodyOrNull<BookmarkRequest>()
            ?.let { req ->
                request.attributeOrNull("userId")
                    ?.let { req.ownerId = it as String }
                    ?.let { bookmarkTransformer.toModel(req) }
                    ?.also { bookmarkRepository.save(it) }
                    ?.let { bookmarkTransformer.fromModel(it) }
                    ?.let { ok().bodyValueAndAwait(it) }
                    ?: status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait("Please login")
            }
            ?: badRequest().bodyValueAndAwait("Empty request body")

    suspend fun modifyBookmark(request: ServerRequest): ServerResponse =
        request.awaitBodyOrNull<BookmarkRequest>()
            ?.let { req ->
                req.id
                    ?.let { bookmarkRepository.findById(it) }
                    ?.also { bookmarkTransformer.updateBookmark(it, req) }
                    ?.also { bookmarkRepository.save(it) }
                    ?.let { bookmarkTransformer.fromModel(it) }
                    ?.let { ok().bodyValueAndAwait(it) }
                    ?: notFound().buildAndAwait()
            } ?: badRequest().bodyValueAndAwait("Empty Request body")

    private suspend fun generateBookmarkId(): String =
        RandomStringUtils.randomAlphanumeric(16)
            .let {
                bookmarkRepository.findById(it)
                    ?.let { generateBookmarkId() }
                    ?: it
            }
}