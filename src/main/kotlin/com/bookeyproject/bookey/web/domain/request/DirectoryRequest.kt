package com.bookeyproject.bookey.web.domain.request

data class DirectoryRequest(
    val name: String,
    val parent: String?,
    var userId: String?
)
