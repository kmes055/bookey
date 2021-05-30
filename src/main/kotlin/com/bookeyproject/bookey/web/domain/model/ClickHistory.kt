package com.bookeyproject.bookey.web.domain.model

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class ClickHistory(
    @Id
    var historyId: Int? = null,
    var bookmarkId: String? = null,
    var clickDateTime: LocalDateTime = now()
)