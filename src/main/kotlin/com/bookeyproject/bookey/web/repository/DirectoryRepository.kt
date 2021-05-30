package com.bookeyproject.bookey.web.repository

import com.bookeyproject.bookey.web.domain.model.Bookmark
import com.bookeyproject.bookey.web.domain.model.Directory
import org.springframework.data.r2dbc.core.*
import org.springframework.data.r2dbc.query.Criteria.where
import org.springframework.stereotype.Repository

@Repository
class DirectoryRepository(private val databaseClient: DatabaseClient) {
    private val tableName = "directory"

    suspend fun findAllByOwnerId(ownerId: String) =
        databaseClient.select()
            .from(tableName)
            .matching(where("ownerId").`is`(ownerId))
            .asType<Directory>()
            .fetch()
            .flow()

    suspend fun findOne(id: String): Directory? =
        databaseClient.select()
            .from(tableName)
            .matching(where("id").`is`(id))
            .asType<Directory>()
            .fetch()
            .awaitFirstOrNull()

    suspend fun save(directory: Directory) =
        databaseClient.insert()
            .into<Directory>()
            .table(tableName)
            .using(directory)
            .await()

    suspend fun findById(id: String): Directory? =
        databaseClient.select()
            .from(tableName)
            .matching(where("id").`is`(id))
            .asType<Directory>()
            .fetch()
            .awaitFirstOrNull()
}