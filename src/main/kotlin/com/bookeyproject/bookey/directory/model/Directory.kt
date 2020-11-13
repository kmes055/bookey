package com.bookeyproject.bookey.directory.model

import com.bookeyproject.bookey.constant.DatabaseConstants.DIRECTORY_ID_LENGTH
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.apache.commons.lang3.StringUtils.EMPTY
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Directory (
        @Id
        private val id: String = randomAlphanumeric(DIRECTORY_ID_LENGTH.value),
        private val name: String = EMPTY,
        private val parent: String = "root",
        private val owner: String = EMPTY
)