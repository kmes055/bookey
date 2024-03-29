package com.bookeyproject.bookey.auth.domain

data class GoogleOAuthToken(
    val accessToken: String,
    val expiresIn: Int,
    val scope: String,
    val tokenType: String = "Bearer",
    val idToken: String
)