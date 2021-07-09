package com.bookeyproject.bookey.web.domain.model

import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.data.annotation.Id
import java.time.OffsetDateTime

data class Directory(
    @Id
    var id: String = EMPTY,
    var name: String = EMPTY,
    var parent: String = EMPTY,
    var userId: String = EMPTY,
    var createdAt: OffsetDateTime = OffsetDateTime.now()
)