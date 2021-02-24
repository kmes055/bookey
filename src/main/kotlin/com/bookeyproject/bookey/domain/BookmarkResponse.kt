package com.bookeyproject.bookey.domain

import org.apache.commons.lang3.StringUtils.EMPTY
import java.time.LocalDateTime

data class BookmarkResponse(
    val bookmarkId: String = EMPTY,
    val name: String = EMPTY,
    val description: String = EMPTY,
    val url: String = EMPTY,
    val directory: String = EMPTY,
    val lastClickDatetime: LocalDateTime? = null
) {
    constructor(bookmark: Bookmark) : this(
        bookmark.bookmarkId,
        bookmark.name,
        bookmark.description,
        bookmark.url,
        bookmark.directory
    )
}