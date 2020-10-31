package com.bookeyproject.bookey.client

interface OAuthClient {
    val baseUrl: String

    fun getRedirectUrl(): String
    fun getOAuthToken(code: String, state: String): String
    fun getUserInfo(token: String): String
}