package com.bookeyproject.bookey.domain

import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class Bookmark(
    @Id
    var bookmarkId: String = EMPTY,
    var name: String = EMPTY,
    var description: String = EMPTY,
    var url: String = EMPTY,
    var directoryId: String = "root",
    var ownerId: String = EMPTY,
    var createDateTime: LocalDateTime = LocalDateTime.now()
)