package com.bookeyproject.bookey.web.domain.request

data class BookmarkRequest(
    val id: String?,
    val title: String?,
    val description: String?,
    val url: String,
    val directory: String?,
    val siteName: String?,
    val favicon: String?,
    val thumbnail: String?,
    val memo: String?,
    var ownerId: String
)
