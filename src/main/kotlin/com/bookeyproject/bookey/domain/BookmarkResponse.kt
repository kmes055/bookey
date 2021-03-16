package com.bookeyproject.bookey.domain

import java.time.OffsetDateTime

data class BookmarkResponse(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val directory: String,
    val siteName: String,
    val favicon: String,
    val thumbnail: String,
    val memo: String,
    val createdAt: OffsetDateTime,
    val clickCount: Int
)