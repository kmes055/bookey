package com.bookeyproject.bookey.bookmark.handler

import com.bookeyproject.bookey.bookmark.domain.BookmarkRequest
import com.bookeyproject.bookey.bookmark.domain.BookmarkResponse
import com.bookeyproject.bookey.bookmark.repository.BookmarkRepository
import com.bookeyproject.bookey.common.domain.StandardResponse
import com.bookeyproject.bookey.common.type.ResponseType
import com.bookeyproject.bookey.service.BookmarkTransformer
import com.bookeyproject.bookey.service.OpenGraphService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

@Service
class BookmarkHandler(
    private val bookmarkRepository: BookmarkRepository,
    private val bookmarkTransformer: BookmarkTransformer
) {
    private val log = KotlinLogging.logger { }

    suspend fun getBookmarks(request: ServerRequest): ServerResponse =
        request.attributeOrNull("userId")
            ?.let { bookmarkRepository.findAllByOwnerId(it as String) }
            ?.map { bookmarkTransformer.fromModel(it) }
            ?.let { StandardResponse(it.toList()) }
            ?.let { ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(it) }
            ?: ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.NOT_FOUND))

    suspend fun getBookmark(request: ServerRequest): ServerResponse =
        bookmarkRepository.findById(request.pathVariable("id"))
            ?.let { bookmarkTransformer.fromModel(it) }
            ?.let { StandardResponse(it) }
            ?.let { ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(it) }
            ?: ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.NOT_FOUND))

    suspend fun addBookmark(request: ServerRequest): ServerResponse =
        request.awaitBodyOrNull<BookmarkRequest>()
            ?.let { req ->
                request.attributeOrNull("userId")
                    ?.let { req.ownerId = it as String }
                    ?.let { bookmarkTransformer.toModel(req) }
                    ?.also { bookmarkRepository.save(it) }
                    ?.let { bookmarkTransformer.fromModel(it) }
                    ?.let { StandardResponse(it) }
                    ?.let { ok().bodyValueAndAwait(it) }
            } ?: ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.BAD_REQUEST))

    suspend fun modifyBookmark(request: ServerRequest): ServerResponse =
        request.awaitBodyOrNull<BookmarkRequest>()
            ?.let { req ->
                req.id
                    ?.let { bookmarkRepository.findById(it) }
                    ?.also { bookmarkTransformer.updateBookmark(it, req) }
                    ?.also { bookmarkRepository.save(it) }
                    ?.let { bookmarkTransformer.fromModel(it) }
                    ?.let { StandardResponse(it) }
                    ?.let { ok().bodyValueAndAwait(it) }
                    ?: ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.NOT_FOUND))
            } ?: ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.BAD_REQUEST))
}