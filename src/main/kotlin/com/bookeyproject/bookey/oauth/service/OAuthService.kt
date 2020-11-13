package com.bookeyproject.bookey.oauth.service

import com.bookeyproject.bookey.oauth.client.GoogleOAuthClient
import com.bookeyproject.bookey.oauth.client.NaverOAuthClient
import com.bookeyproject.bookey.oauth.client.OAuthClient
import com.bookeyproject.bookey.oauth.constant.OAuthProvider
import com.bookeyproject.bookey.oauth.constant.OAuthProvider.*
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
class OAuthService(
        private val googleOAuthClient: GoogleOAuthClient,
        private val naverOAuthClient: NaverOAuthClient
) {
    private val log = KotlinLogging.logger {}

    fun getRedirectUrl(oAuthProvider: OAuthProvider): String {
        log.debug("redirect URL: {}", getClient(oAuthProvider).getRedirectUrl())
        return getClient(oAuthProvider).getRedirectUrl()
    }

    fun processLogin(provider: OAuthProvider, code: String, state: String): String {
        val client = getClient(provider)
        val token = client.getOAuthToken(code, state)

        return client.getUserInfo(token)
    }

    private fun getClient(oAuthProvider: OAuthProvider): OAuthClient {
        return when(oAuthProvider) {
            GOOGLE -> googleOAuthClient
            NAVER -> naverOAuthClient
            else -> throw IllegalArgumentException()
        }
    }
}