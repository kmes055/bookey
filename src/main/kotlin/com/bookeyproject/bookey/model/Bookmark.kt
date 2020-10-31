package com.bookeyproject.bookey.model

import com.bookeyproject.bookey.constant.DatabaseConstants
import javax.persistence.Entity
import javax.persistence.Id

import java.time.LocalDateTime

import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric

@Entity
data class Bookmark (
        @Id
        private val bookmarkId: String = randomAlphanumeric(DatabaseConstants.BOOKMARK_ID_LENGTH.value),
        private val name: String? = null,
        private val description: String? = null,
        private val url: String = "https://bookey.io",
        private val directoryId: String? = null,
        private val ownerId: String? = null,
        private val createDateTime: LocalDateTime = LocalDateTime.now()
) {
}