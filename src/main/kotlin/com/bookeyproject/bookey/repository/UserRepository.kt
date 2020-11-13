package com.bookeyproject.bookey.repository

import com.bookeyproject.bookey.model.BookeyUser
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<BookeyUser, String> {

    fun findByGoogleId(googleId: String): List<BookeyUser>
}