package com.bookeyproject.bookey.bookmark.domain


data class BookmarkRequest(
    val id: String?,
    val memo: String?,
    val url: String,
    val directory: String?,
    var ownerId: String
)