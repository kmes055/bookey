package com.bookeyproject.bookey.model

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class BookeyUser (
    @Id
    val userId: String = UUID.randomUUID().toString(),
    val googleId: String? = null,
    val naverId: String? = null,
    val kakaoId: String? = null,
    val nickname: String? = null,
    val registerDatetime: LocalDateTime = LocalDateTime.now()
)