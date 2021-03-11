package com.bookeyproject.bookey.repository

import com.bookeyproject.bookey.domain.Bookmark
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.core.*
import org.springframework.stereotype.Repository

@Repository
class BookmarkRepository(
        private val client: DatabaseClient
) {
    private final val tableName = "bookmark"

    fun findAllByOwnerId(ownerId: String): Flow<Bookmark> =
            client.execute("SELECT * FROM $tableName WHERE owner_id = :ownerId")
                    .bind("ownerId", ownerId)
                    .asType<Bookmark>()
                    .fetch()
                    .flow()

    suspend fun findById(id: String): Bookmark? =
            client.execute("SELECT * from $tableName where id = :id")
                    .bind("id", id)
                    .asType<Bookmark>()
                    .fetch()
                    .awaitFirstOrNull()

    suspend fun save(bookmark: Bookmark) =
            client.insert()
                    .into<Bookmark>()
                    .table("bookmark")
                    .using(bookmark)
                    .await()
}