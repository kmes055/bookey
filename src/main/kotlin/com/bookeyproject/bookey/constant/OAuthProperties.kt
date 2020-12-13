package com.bookeyproject.bookey.constant

enum class OAuthProperties(val value: String) {
    CLIENT_ID("client_id"),
    CLIENT_SECRET("client_secret"),
    REDIRECT_URI("redirect_uri"),
    RESPONSE_TYPE("response_type"),
    GRANT_TYPE("grant_type"),
    CODE("code"),
    STATE("state"),
    SCOPE("scope"),
}