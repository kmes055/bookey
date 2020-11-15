package com.bookeyproject.bookey.directory.model

import org.apache.commons.lang3.StringUtils.EMPTY
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Directory (
        @Id
        private val id: String = EMPTY,
        private val name: String = EMPTY,
        private val parent: String = "root",
        private val owner: String = EMPTY
)