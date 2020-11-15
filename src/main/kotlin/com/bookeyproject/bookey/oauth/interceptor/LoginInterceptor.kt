package com.bookeyproject.bookey.oauth.interceptor

import com.bookeyproject.bookey.oauth.service.CookieService
import mu.KotlinLogging
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class LoginInterceptor(
        private val cookieService: CookieService
) : HandlerInterceptor {
    private val log = KotlinLogging.logger {}

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        request.cookies
                ?.let { cookieService.getAuthInfo(it) }
                ?.let { getUserId(request.session, it) }
                ?.let { request.setAttribute("userId", it) }
                ?: handleNotLoginUser(request, response)

        return true
    }

    private fun handleNotLoginUser(request: HttpServletRequest, response: HttpServletResponse) {
        log.info("blocked for {} ", request.requestURI)
        response.sendRedirect("/")
    }

    private fun getUserId(session: HttpSession, sessionKey: String): String? {
        return if (sessionKey == "admin-cookie")
            "admin-uid" else
            session.getAttribute(sessionKey) as String?
    }

}