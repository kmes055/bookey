package com.bookeyproject.bookey.service

import org.springframework.stereotype.Service
import javax.servlet.http.Cookie

@Service
class CookieService {
    private final val oauthCookieName = "BKY_SES"

    fun extractAuthCookieValue(cookies: Array<Cookie>): String? {
        return cookies.toList()
                .firstOrNull { isAuthCookie(it) }
                ?.value
    }

    fun createAuthCookie(sessionKey: String): Cookie {
        return Cookie(oauthCookieName, sessionKey).apply {
            this.domain = "boo-key.com"
            this.maxAge = -1
            this.path = "/"
        }
    }

    fun isAuthCookie(cookie: Cookie): Boolean {
        return cookie.name == oauthCookieName
    }

}