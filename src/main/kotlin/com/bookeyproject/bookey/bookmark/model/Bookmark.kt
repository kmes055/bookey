package com.bookeyproject.bookey.bookmark.model

import javax.persistence.Entity
import javax.persistence.Id

import java.time.LocalDateTime

import org.apache.commons.lang3.StringUtils.EMPTY

@Entity
data class Bookmark (
        @Id
        val bookmarkId: String = EMPTY,
        val name: String = EMPTY,
        val description: String = EMPTY,
        val url: String = EMPTY,
        val directoryId: String = "root",
        val ownerId: String = EMPTY,
        val createDateTime: LocalDateTime = LocalDateTime.now()
)