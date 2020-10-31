package com.bookeyproject.bookey.model

data class GoogleOAuthToken(
        val accessToken: String,
        val expiresIn: Int,
        val refreshToken: String,
        val scope: String,
        val tokenType: String = "Bearer"
)