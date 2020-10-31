package com.bookeyproject.bookey.model

import java.time.LocalDateTime
import java.time.LocalDateTime.now
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class ClickHistory (
        @Id
        private val historyId: Int? = null,
        private val bookmarkId: String? = null,
        private val clickDateTime: LocalDateTime = now()
)