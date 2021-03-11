package com.bookeyproject.bookey.domain


data class BookmarkRequest(
    val id: String?,
    val memo: String?,
    val url: String,
    val directory: String?,
    var ownerId: String
)