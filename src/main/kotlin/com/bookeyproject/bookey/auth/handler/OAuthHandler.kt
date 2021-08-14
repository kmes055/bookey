package com.bookeyproject.bookey.auth.handler

import com.bookeyproject.bookey.auth.client.OAuthClient
import com.bookeyproject.bookey.auth.constant.OAuthProvider
import com.bookeyproject.bookey.auth.domain.BookeyUser
import com.bookeyproject.bookey.auth.exception.LoginException
import com.bookeyproject.bookey.auth.repository.UserRepository
import io.netty.handler.codec.http.cookie.CookieHeaderNames
import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.server.WebSession
import java.net.URI

@Component
class OAuthHandler(
    private val googleOAuthClient: OAuthClient,
    private val naverOAuthClient: OAuthClient,
    private val userRepository: UserRepository,
    @Value("\${bookey.web.host}")
    private val bookeyWebHost: String
) {
    companion object {
        const val PROVIDER = "provider"
        const val CODE = "code"
        const val STATE = "state"
        const val SESSION_COOKIE_NAME = "BKY_SES"
    }

    private val log = KotlinLogging.logger { }

    suspend fun redirect(request: ServerRequest): ServerResponse =
        request.pathVariable(PROVIDER)
            .also { log.debug("I am here~!!!!!!") }
            .let { OAuthProvider.of(it) }
            ?.let { temporaryRedirect(getOAuthRedirectURI(it)).buildAndAwait() }
            ?: badRequest().bodyValueAndAwait("Provider not found")

    suspend fun handleCallback(request: ServerRequest): ServerResponse =
        request.pathVariable(PROVIDER)
            .let { OAuthProvider.of(it) }
            ?.let { provider ->
                fetchUserInfo(
                    provider,
                    request.queryParamOrNull(CODE) ?: throw LoginException("Failed to login: no code"),
                    request.queryParamOrNull(STATE) ?: ""
                )
                    .let { getOrRegister(provider, it) }
            }
            ?.let { setSessionAndCookie(request.awaitSession(), it) }
            ?: throw LoginException("#### Should not reach here: handle callback")

    private suspend fun fetchUserInfo(provider: OAuthProvider, code: String, state: String): String =
        getClient(provider)
            .let { client ->
                client.getOAuthToken(code, state)
                    .let { client.getUserInfo(it) }
            }

    @Transactional
    private suspend fun setSessionAndCookie(session: WebSession, user: BookeyUser): ServerResponse =
        RandomStringUtils.randomAlphanumeric(32)
            .also { sessionId -> session.attributes[sessionId] = user.userId }
            .let { createAuthCookie(it) }
            .let { permanentRedirect(toBookeyWebURI(user)).cookie(it) }
            .buildAndAwait()

    private suspend fun getOAuthRedirectURI(oAuthProvider: OAuthProvider): URI =
        getClient(oAuthProvider)
            .getRedirectUrl()
            .also { log.debug("redirect URL: {}", it) }
            .let { URI(it) }

    private suspend fun toBookeyWebURI(user: BookeyUser): URI =
        URI(bookeyWebHost + if (user.nickname.isNullOrEmpty()) "/logon" else "/")

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
            else -> throw InvalidDataAccessApiUsageException("Invalid provider type")
        }
            ?: register(provider, userId)

    private suspend fun register(provider: OAuthProvider, userId: String): BookeyUser =
        when (provider) {
            OAuthProvider.GOOGLE -> BookeyUser(googleId = userId)
            OAuthProvider.NAVER -> BookeyUser(naverId = userId)
            OAuthProvider.KAKAO -> BookeyUser(kakaoId = userId)
            else -> throw InvalidDataAccessApiUsageException("Invalid provider type")
        }.also { userRepository.save(it) }

    private suspend fun createAuthCookie(sessionId: String): ResponseCookie =
        ResponseCookie.from(SESSION_COOKIE_NAME, sessionId)
            .domain(".boo-key.com")
            .httpOnly(true)
            .maxAge(-1)
            .path("/")
            .secure(true)
            .sameSite(CookieHeaderNames.SameSite.Lax.name)
            .build()
}