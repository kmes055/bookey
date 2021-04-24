package com.bookeyproject.bookey.directory.domain

import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.data.annotation.Id

data class Directory(
    @Id
    var id: String = EMPTY,
    var name: String = EMPTY,
    var parent: String = "root",
    var owner: String = EMPTY
)