package com.bookeyproject.bookey.domain

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.time.LocalDateTime.now

data class ClickHistory (
        @Id
        private val historyId: Int? = null,
        private val bookmarkId: String? = null,
        private val clickDateTime: LocalDateTime = now()
)