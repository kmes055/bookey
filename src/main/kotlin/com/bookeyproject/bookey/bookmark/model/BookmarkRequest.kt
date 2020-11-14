package com.bookeyproject.bookey.bookmark.model

import org.apache.commons.lang3.StringUtils.EMPTY
import java.time.LocalDateTime


data class BookmarkRequest(
        val bookmarkId: String?,
        val name: String,
        val description: String,
        val url: String,
        val directory: String?,
        val ownerId: String,
        val createDatetime: LocalDateTime? = LocalDateTime.now()
)