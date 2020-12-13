package com.bookeyproject.bookey.domain

import org.apache.commons.lang3.StringUtils.EMPTY
import java.time.LocalDateTime

data class BookmarkResponse (
        private val bookmarkId: String = EMPTY,
        private val name: String = EMPTY,
        private val description: String = EMPTY,
        private val url: String = EMPTY,
        private val directory: String = EMPTY,
        private val lastClickDatetime: LocalDateTime? = null
) {
    constructor(bookmark: Bookmark): this(
                bookmark.bookmarkId,
                bookmark.name,
                bookmark.description,
                bookmark.url,
                bookmark.directoryId
        )
}