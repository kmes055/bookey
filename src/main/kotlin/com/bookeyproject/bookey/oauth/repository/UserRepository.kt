package com.bookeyproject.bookey.oauth.repository

import com.bookeyproject.bookey.oauth.model.BookeyUser
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<BookeyUser, String> {

    fun findByGoogleId(googleId: String): List<BookeyUser>
}