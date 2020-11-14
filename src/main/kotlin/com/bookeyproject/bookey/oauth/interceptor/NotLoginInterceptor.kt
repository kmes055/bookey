package com.bookeyproject.bookey.oauth.interceptor

import com.bookeyproject.bookey.oauth.service.CookieService
import mu.KotlinLogging
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class NotLoginInterceptor(
        private val cookieService: CookieService
): HandlerInterceptor {
    private val log = KotlinLogging.logger {}

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.cookies
                .let(cookieService::getAuthInfo)
                ?.let { getUserId(request.session, it) }
                ?.let { handleLoginUser(request, response) }

        return true
    }

    private fun handleLoginUser(request: HttpServletRequest, response: HttpServletResponse) {
        log.info("blocked for {} ", request.requestURI)
        response.sendRedirect("/")
    }

    private fun getUserId(session: HttpSession, sessionKey: String): String? {
        return session.getAttribute(sessionKey) as String?
    }

}