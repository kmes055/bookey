package com.bookeyproject.bookey.web.handler

import com.bookeyproject.bookey.web.domain.request.BookmarkRequest
import com.bookeyproject.bookey.web.repository.BookmarkRepository
import com.bookeyproject.bookey.web.domain.StandardResponse
import com.bookeyproject.bookey.web.type.ResponseType
import com.bookeyproject.bookey.web.service.BookmarkTransformer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

@Component
class BookmarkHandler(
    private val bookmarkRepository: BookmarkRepository,
    private val bookmarkTransformer: BookmarkTransformer
) {
    private val log = KotlinLogging.logger { }

    suspend fun getBookmarks(request: ServerRequest): ServerResponse =
        request.attributeOrNull("userId")
            ?.let { bookmarkRepository.findAllByUserId(it as String) }
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
            ?.also { it.userId = request.attributeOrNull("userId") as String }
            ?.let { bookmarkTransformer.toModel(it) }
            ?.also { bookmarkRepository.save(it) }
            ?.let { bookmarkTransformer.fromModel(it) }
            ?.let { StandardResponse(it) }
            ?.let { ok().bodyValueAndAwait(it) }
            ?: ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.BAD_REQUEST))

    suspend fun modifyBookmark(request: ServerRequest): ServerResponse =
        request.awaitBodyOrNull<BookmarkRequest>()
            ?.let { req ->
                req.id
                    ?.let { bookmarkRepository.findById(it) }
                    ?.also { bookmarkTransformer.toModifiedModel(it, req) }
                    ?.also { bookmarkRepository.save(it) }
                    ?.let { bookmarkTransformer.fromModel(it) }
                    ?.let { StandardResponse(it) }
                    ?.let { ok().bodyValueAndAwait(it) }
                    ?: ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.NOT_FOUND)) }
            ?: ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.BAD_REQUEST))
}