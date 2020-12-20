package com.bookeyproject.bookey.domain

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