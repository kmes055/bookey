package com.bookeyproject.bookey.repository

import com.bookeyproject.bookey.model.BookeyUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface UserRepository : JpaRepository<BookeyUser, String>, JpaSpecificationExecutor<BookeyUser> {


}