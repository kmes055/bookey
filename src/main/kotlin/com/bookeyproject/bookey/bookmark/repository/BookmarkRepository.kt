package com.bookeyproject.bookey.bookmark.repository

import com.bookeyproject.bookey.bookmark.model.Bookmark
import org.springframework.data.jpa.repository.JpaRepository
import reactor.core.publisher.Flux

interface BookmarkRepository : JpaRepository<Bookmark, String> {

    fun findAllByOwnerId(ownerId: String): List<Bookmark>?
}