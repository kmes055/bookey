package com.bookeyproject.bookey.auth.domain

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.*

data class BookeyUser(
    @Id
    var userId: String = UUID.randomUUID().toString(),
    var googleId: String? = null,
    var naverId: String? = null,
    var kakaoId: String? = null,
    var nickname: String? = null,
    var registerDatetime: LocalDateTime = LocalDateTime.now()
)