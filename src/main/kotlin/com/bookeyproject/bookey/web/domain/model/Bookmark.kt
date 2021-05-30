package com.bookeyproject.bookey.web.domain.model

import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.data.annotation.Id
import java.time.OffsetDateTime

data class Bookmark(
    @Id
    var id: String = EMPTY,
    var ownerId: String = EMPTY,
    var title: String = EMPTY,
    var description: String = EMPTY,
    var url: String = EMPTY,
    var directory: String = "",
    var siteName: String = EMPTY,
    var favicon: String = EMPTY,
    var thumbnail: String = EMPTY,
    var type: String = EMPTY,
    var memo: String = EMPTY,
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
    var clickCount: Int = 0
)