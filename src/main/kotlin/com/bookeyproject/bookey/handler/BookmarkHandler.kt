package com.bookeyproject.bookey.handler

import com.bookeyproject.bookey.domain.Bookmark
import com.bookeyproject.bookey.domain.BookmarkRequest
import com.bookeyproject.bookey.domain.BookmarkResponse
import com.bookeyproject.bookey.repository.BookmarkRepository
import kotlinx.coroutines.flow.map
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

@Service
class BookmarkHandler(
        private val bookmarkRepository: BookmarkRepository
) {
    suspend fun getBookmarks(request: ServerRequest): ServerResponse =
            request.attribute("userId").orElse(null)
                    ?.let { bookmarkRepository.findAllByOwnerId(it as String) }
                    ?.map { BookmarkResponse(it) }
                    ?.let { ok().bodyAndAwait(it) }
                    ?: notFound().buildAndAwait()

    suspend fun getBookmark(request: ServerRequest): ServerResponse =
        bookmarkRepository.findById(request.pathVariable("id"))
                ?.let { BookmarkResponse(it) }
                ?.let { ok().bodyValueAndAwait(it)}
                ?: notFound().buildAndAwait()

    suspend fun addBookmark(request: ServerRequest): ServerResponse =
            request.awaitBodyOrNull<BookmarkRequest>()
                    ?.let { toEntity(it) }
                    ?.also { bookmarkRepository.save(it) }
                    ?.let { BookmarkResponse(it) }
                    ?.let { ok().bodyValueAndAwait(it)}
                    ?: badRequest().bodyValueAndAwait("Empty request body")

    suspend fun modifyBookmark(request: ServerRequest): ServerResponse =
            request.awaitBodyOrNull<BookmarkRequest>()
                    ?.let { req ->
                        req.bookmarkId
                                ?.let { bookmarkRepository.findById(it) }
                                ?.let { updateBookmark(it, req) }
                                ?.also { bookmarkRepository.save(it) }
                                ?.let { BookmarkResponse(it) }
                                ?.let { ok().bodyValueAndAwait(it) }
                                ?: notFound().buildAndAwait()
                         }?: badRequest().bodyValueAndAwait("Empty Request body")


    private suspend fun toEntity(request: BookmarkRequest): Bookmark =
            Bookmark(
                    generateBookmarkId(),
                    request.name,
                    request.description,
                    request.url,
                    request.directory ?: "root",
                    request.ownerId
            )

    private suspend fun generateBookmarkId(): String =
            RandomStringUtils.randomAlphanumeric(16)
                    .let {
                        bookmarkRepository.findById(it)
                                ?.let { generateBookmarkId() }
                                ?: it
                    }

    private suspend fun updateBookmark(bookmark: Bookmark, req: BookmarkRequest) =
            bookmark.apply {
                name = req.name
                description = req.description
                url = req.url
                directoryId = req.directory ?: directoryId
            }
}