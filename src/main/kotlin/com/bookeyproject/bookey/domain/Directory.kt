package com.bookeyproject.bookey.domain

import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.data.annotation.Id

data class Directory (
        @Id
        private val id: String = EMPTY,
        private val name: String = EMPTY,
        private val parent: String = "root",
        private val owner: String = EMPTY
)