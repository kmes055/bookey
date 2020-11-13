package com.bookeyproject.bookey.oauth.client

interface OAuthClient {
    val baseUrl: String

    fun getRedirectUrl(): String
    fun getOAuthToken(code: String, state: String): String
    fun getUserInfo(token: String): String
}