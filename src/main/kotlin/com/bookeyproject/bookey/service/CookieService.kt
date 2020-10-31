package com.bookeyproject.bookey.service

import org.springframework.stereotype.Service
import javax.servlet.http.Cookie

@Service
class CookieService {
    private final val oauthCookieName = "BKY_SES"

    fun createAuthCookie(sessionKey: String): Cookie {
        return Cookie(oauthCookieName, sessionKey).apply {
            this.maxAge = 60*60*24
            this.path = "/"
        }
    }

    fun findAuthCookie(cookies: List<Cookie>): Cookie? {
        return cookies.firstOrNull(this::isAuthCookie)
    }

    private fun isAuthCookie(cookie: Cookie): Boolean {
        return cookie.name == oauthCookieName
    }

}