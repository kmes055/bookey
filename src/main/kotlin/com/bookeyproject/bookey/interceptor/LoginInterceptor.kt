package com.bookeyproject.bookey.interceptor

import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class LoginInterceptor: HandlerInterceptor {
    private val headerName = "X-BOOKEY-AUTH-TOKEN"
    private val tempToken = "AccessToken"

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        Optional.ofNullable(request.getHeader(headerName))
                .map { checkSession(request.session, it) }
                .map(this::decodeToken)
                .ifPresentOrElse(
                        { request.setAttribute("userId", it) },
                        { response.sendRedirect("/landing") }
                )

        return true
    }

    private fun decodeToken(token: String): String {
        return token
    }

    private fun checkSession(session: HttpSession, sessionKey: String): String {
        return session.getAttribute(sessionKey) as String
    }

}