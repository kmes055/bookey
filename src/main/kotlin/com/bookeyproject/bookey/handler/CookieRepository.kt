package com.bookeyproject.bookey.handler

import org.springframework.http.HttpCookie
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import javax.servlet.http.Cookie

@Repository
class CookieRepository {
    private final val oauthCookieName = "BKY_SES"

    fun getAuthInfo(cookies: MultiValueMap<String, HttpCookie>): String? {
        return cookies
                .getFirst(oauthCookieName)
                ?.value
    }

    fun createAuthCookie(sessionKey: String): Cookie {
        return Cookie(oauthCookieName, sessionKey).apply {
            this.domain = ".boo-key.com"
            this.maxAge = -1
            this.path = "/"
        }
    }
}