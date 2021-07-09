package com.bookeyproject.bookey.web.domain.response

import com.bookeyproject.bookey.web.domain.model.Directory
import java.time.OffsetDateTime

data class DirectoryResponse(
    val id: String,
    val name: String,
    val parent: String?,
    val userId: String,
    val createdAt: OffsetDateTime
) {
    constructor(directory: Directory) : this(
        directory.id,
        directory.name,
        directory.parent,
        directory.userId,
        directory.createdAt
    )
}
