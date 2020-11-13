package com.bookeyproject.bookey.oauth.constant

enum class OAuthProvider(val columnName: String) {
    GOOGLE("google_id"),
    NAVER("naver_id"),
    FACEBOOK("facebook_id"),
    KAKAO("kakao_id")
}