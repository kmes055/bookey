package com.bookeyproject.bookey.bookmark.controller

import com.bookeyproject.bookey.bookmark.model.BookmarkRequest
import com.bookeyproject.bookey.bookmark.model.BookmarkResponse
import com.bookeyproject.bookey.bookmark.service.BookmarkService
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController("v1.0/bookmarks")
class BookmarkController(
        private val bookmarkService: BookmarkService
) {
    @GetMapping
    fun getBookmarks(request: HttpServletRequest): List<BookmarkResponse> {
        return request.getAttribute("userId")
                ?.let { bookmarkService.getBookmarks(it as String) }
                ?: emptyList()
    }

    @PostMapping
    fun addBookmark(@RequestBody bookmarkRequest: BookmarkRequest): BookmarkResponse {
        return bookmarkService.addBookmark(bookmarkRequest)
    }

    @PutMapping
    fun modifyBookmark(@RequestBody bookmarkRequest: BookmarkRequest): BookmarkResponse {
        return bookmarkService.modifyBookmark(bookmarkRequest)
    }
}