package com.bookeyproject.bookey.web.repository

import com.bookeyproject.bookey.web.domain.model.Bookmark
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.core.*
import org.springframework.data.r2dbc.query.Criteria.where
import org.springframework.stereotype.Repository

@Repository
class BookmarkRepository(
    private val client: DatabaseClient
) {
    private final val tableName = "bookmark"

    fun findAllByUserId(userId: String): Flow<Bookmark> =
        client.select()
            .from(tableName)
            .matching(where("userId").`is`(userId))
            .asType<Bookmark>()
            .fetch()
            .flow()

    suspend fun findById(id: String): Bookmark? =
        client.select().from(tableName)
            .matching(where("id").`is`(id))
            .asType<Bookmark>()
            .fetch()
            .awaitFirstOrNull()

    suspend fun save(bookmark: Bookmark) =
        client.insert()
            .into<Bookmark>()
            .table(tableName)
            .using(bookmark)
            .await()
}