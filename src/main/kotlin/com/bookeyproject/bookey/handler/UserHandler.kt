package com.bookeyproject.bookey.handler

import com.bookeyproject.bookey.constant.OAuthProvider
import com.bookeyproject.bookey.domain.BookeyUser
import com.bookeyproject.bookey.repository.UserRepository
import mu.KotlinLogging
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Service
class UserHandler(
    private val userRepository: UserRepository
) {
    private val log = KotlinLogging.logger {}

    suspend fun getUserInfo(request: ServerRequest): ServerResponse =
        request.attributeOrNull("userId")
            ?.let { it as String }
            ?.let { userId ->
                "google"
                    ?.let { OAuthProvider.of(it) }
                    ?.let { provider ->
                        getUserInfoWithProvider(provider, userId)
                            ?: register(provider, userId)
                    } ?: badRequest().bodyValueAndAwait("Invalid provider name")
            }?.let { ok().bodyValueAndAwait(it) }
            ?: badRequest().bodyValueAndAwait("No user authentication information found")

    suspend fun getUserInfoWithProvider(provider: OAuthProvider, userId: String): BookeyUser? =
        when (provider) {
            OAuthProvider.GOOGLE -> userRepository.findByGoogleId(userId)
            else -> null
        }

    suspend fun register(provider: OAuthProvider, userId: String): BookeyUser =
        when (provider) {
            OAuthProvider.GOOGLE -> BookeyUser(googleId = userId)
            OAuthProvider.NAVER -> BookeyUser(naverId = userId)
            OAuthProvider.KAKAO -> BookeyUser(kakaoId = userId)
            else -> throw InvalidDataAccessApiUsageException("Invalid provider type")
        }.also { userRepository.save(it) }

    suspend fun setNickname(request: ServerRequest): ServerResponse {
        request.headers().header("x-bookey-id").firstOrNull()
            ?.let { userId ->
                userRepository.findById(userId)
                    ?.also {
                        it.nickname = request.queryParamOrNull("nickname")
                        userRepository.update(it)
                    }
                    ?: throw InvalidDataAccessApiUsageException("No user found with userId $userId")
            }
            ?: throw IllegalArgumentException("No userId in request")

        return ok().bodyValueAndAwait(true)
    }
}
