package com.bookeyproject.bookey.auth.constant

enum class OAuthProvider(val columnName: String) {
    GOOGLE("google_id"),
    NAVER("naver_id"),
    FACEBOOK("facebook_id"),
    KAKAO("kakao_id");


    companion object {
        private val BY_ID = HashMap<String, OAuthProvider>()

        init {
            values().forEach {
                BY_ID[it.name.toLowerCase()] = it
            }
        }

        fun of(name: String): OAuthProvider? = BY_ID[name]
    }
}