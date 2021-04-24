package com.bookeyproject.bookey.oauth.handler

import com.bookeyproject.bookey.oauth.client.OAuthClient
import com.bookeyproject.bookey.oauth.constant.OAuthProvider
import com.bookeyproject.bookey.oauth.domain.BookeyUser
import com.bookeyproject.bookey.oauth.exception.LoginException
import com.bookeyproject.bookey.oauth.repository.UserRepository
import io.netty.handler.codec.http.cookie.CookieHeaderNames
import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.server.WebSession
import java.net.URI

@Service
class OAuthHandler(
    private val googleOAuthClient: OAuthClient,
    private val naverOAuthClient: OAuthClient,
    private val userRepository: UserRepository
) {
    private val log = KotlinLogging.logger { }

    suspend fun redirect(request: ServerRequest): ServerResponse =
        request.pathVariable("provider")
            .let { OAuthProvider.of(it) }
            ?.let { permanentRedirect(getOAuthRedirectURI(it)) }
            ?.buildAndAwait()
            ?: badRequest().bodyValueAndAwait("Provider not found")

    suspend fun handleCallback(request: ServerRequest): ServerResponse =
        request.pathVariable("provider")
            .let { OAuthProvider.of(it) }
            ?.let { provider ->
                processLogin(
                    provider,
                    request.queryParamOrNull("code") ?: throw LoginException("Failed to login: no code"),
                    request.queryParamOrNull("state") ?: StringUtils.EMPTY)
                .let { getOrRegister(provider, it) } }
            ?.let { configureAuthInfo(request.awaitSession(), it) }
                ?: throw LoginException("Should not reach here: handle callback")

    @Transactional
    private suspend fun configureAuthInfo(session: WebSession, user: BookeyUser): ServerResponse =
        RandomStringUtils.randomAlphanumeric(32)
            .also { session.attributes[it] = user.userId }
            .let { createAuthCookie(it) }
            .let { permanentRedirect(getBookeyRedirectURI(user)).cookie(it) }
            .buildAndAwait()

    private suspend fun getOAuthRedirectURI(oAuthProvider: OAuthProvider): URI =
        getClient(oAuthProvider)
            .getRedirectUrl()
            .also { log.debug("redirect URL: {}", it) }
            .let { URI(it) }

    private suspend fun getBookeyRedirectURI(user: BookeyUser): URI =
        URI(if (user.nickname.isNullOrEmpty()) "/login" else "/")

    private suspend fun processLogin(provider: OAuthProvider, code: String, state: String): String =
        getClient(provider)
            .let { client ->
                client.getOAuthToken(code, state)
                    .let { client.getUserInfo(it) }
            }

    private fun getClient(oAuthProvider: OAuthProvider): OAuthClient {
        return when (oAuthProvider) {
            OAuthProvider.GOOGLE -> googleOAuthClient
            OAuthProvider.NAVER -> naverOAuthClient
            else -> throw IllegalArgumentException()
        }
    }

    private suspend fun getOrRegister(provider: OAuthProvider, userId: String): BookeyUser =
        when (provider) {
            OAuthProvider.GOOGLE -> userRepository.findByGoogleId(userId)
            else -> throw InvalidDataAccessApiUsageException("Invalid provider type") }
            ?: register(provider, userId)

    private suspend fun register(provider: OAuthProvider, userId: String): BookeyUser =
        when (provider) {
            OAuthProvider.GOOGLE -> BookeyUser(googleId = userId)
            OAuthProvider.NAVER -> BookeyUser(naverId = userId)
            OAuthProvider.KAKAO -> BookeyUser(kakaoId = userId)
            else -> throw InvalidDataAccessApiUsageException("Invalid provider type")
        }.also { userRepository.save(it) }

    private suspend fun createAuthCookie(sessionId: String): ResponseCookie =
        ResponseCookie.from("BKY_SES", sessionId)
            .domain("boo-key.com")
            .httpOnly(true)
            .maxAge(-1)
            .path("/")
            .secure(true)
            .sameSite(CookieHeaderNames.SameSite.Lax.name)
            .build()

    suspend fun setNickname(userId: String, nickname: String) {
        userRepository.findById(userId)
            ?.copy(nickname = nickname)
            ?.run { userRepository.save(this) }
    }
}