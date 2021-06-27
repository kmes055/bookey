package com.bookeyproject.bookey.auth.repository

import com.bookeyproject.bookey.auth.constant.OAuthProvider
import com.bookeyproject.bookey.auth.domain.BookeyUser
import org.springframework.data.r2dbc.core.*
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val client: DatabaseClient
) {
    suspend fun findBy(provider: OAuthProvider, id: String): BookeyUser? =
        when (provider) {
            OAuthProvider.GOOGLE -> findByGoogleId(id)
            else -> null
        }

    suspend fun findByGoogleId(googleId: String): BookeyUser? =
        client.execute("SELECT * FROM bookey_user WHERE google_id = :googleId")
            .bind("googleId", googleId)
            .asType<BookeyUser>()
            .fetch()
            .awaitOneOrNull()

    suspend fun findById(id: String): BookeyUser? =
        client.execute("SELECT * FROM bookey_user WHERE user_id = :id")
            .bind("id", id)
            .asType<BookeyUser>()
            .fetch()
            .awaitOneOrNull()

    suspend fun save(user: BookeyUser) =
        client.insert()
            .into<BookeyUser>()
            .table("bookey_user")
            .using(user)
            .await()

    suspend fun update(user: BookeyUser) =
        client.update()
            .table(BookeyUser::class.java)
            .using(user)
            .fetch()
            .awaitRowsUpdated()
}