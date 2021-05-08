package com.bookeyproject.bookey.bookmark.repository

import com.bookeyproject.bookey.bookmark.domain.Bookmark
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.core.*
import org.springframework.data.r2dbc.query.Criteria.where
import org.springframework.stereotype.Repository

@Repository
class BookmarkRepository(
    private val client: DatabaseClient
) {
    private final val tableName = "bookmark"

    fun findAllByOwnerId(ownerId: String): Flow<Bookmark> =
        client.select()
            .from(tableName)
            .matching(where("ownerId").`is`(ownerId))
            .asType<Bookmark>()
            .fetch()
            .flow()

    suspend fun findById(id: String): Bookmark? =
        client.select().from(tableName)
            .matching(where("id").`is`(id))
            .asType<Bookmark>()
            .fetch()
            .awaitFirst()

    suspend fun save(bookmark: Bookmark) =
        client.insert()
            .into<Bookmark>()
            .table(tableName)
            .using(bookmark)
            .await()
}