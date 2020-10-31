package com.bookeyproject.bookey.service

import com.bookeyproject.bookey.client.GoogleOAuthClient
import com.bookeyproject.bookey.client.NaverOAuthClient
import com.bookeyproject.bookey.client.OAuthClient
import com.bookeyproject.bookey.constant.OAuthProvider
import com.bookeyproject.bookey.constant.OAuthProvider.*
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
class OAuthService(
        private val googleOAuthClient: GoogleOAuthClient,
        private val naverOAuthClient: NaverOAuthClient
) {

    fun getRedirectUrl(oAuthProvider: OAuthProvider): String {
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