package com.bookeyproject.bookey.bookmark.service

import com.bookeyproject.bookey.bookmark.model.Bookmark
import com.bookeyproject.bookey.bookmark.model.BookmarkRequest
import com.bookeyproject.bookey.bookmark.model.BookmarkResponse
import com.bookeyproject.bookey.bookmark.repository.BookmarkRepository
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

@Service
class BookmarkService(
        private val bookmarkRepository: BookmarkRepository
) {
    fun getBookmarks(userId: String): List<BookmarkResponse> {
        return bookmarkRepository.findAllByOwnerId(userId)
                ?.map { BookmarkResponse(it) }
                ?: emptyList()
    }

    fun addBookmark(bookmarkRequest: BookmarkRequest): BookmarkResponse {
        return toEntity(bookmarkRequest)
                .let { bookmarkRepository.save(it) }
                .let { BookmarkResponse(it) }
    }

    fun modifyBookmark(bookmarkRequest: BookmarkRequest): BookmarkResponse {
        return bookmarkRequest.bookmarkId
                ?.let { bookmarkRepository.findById(it).get() }
                ?.let { toEntity(bookmarkRequest) }
                ?.let { bookmarkRepository.save(it) }
                ?.let { BookmarkResponse(it) }
                ?: BookmarkResponse()
    }

    private val toEntity: (BookmarkRequest) -> Bookmark = {
        request -> Bookmark(
            generateBookmarkId(),
            request.name,
            request.description,
            request.url,
            request.directory ?: "root",
            request.ownerId
         )
    }

    private fun generateBookmarkId(): String {
        return RandomStringUtils.randomAlphanumeric(16)
                .let { bookmarkRepository.findById(it) }
                .map { it.bookmarkId }
                .orElse(generateBookmarkId())
    }
}