package com.bookeyproject.bookey.controller

import com.bookeyproject.bookey.constant.OAuthProvider.GOOGLE
import com.bookeyproject.bookey.service.CookieService
import com.bookeyproject.bookey.service.OAuthService
import com.bookeyproject.bookey.service.UserService
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.transaction.Transactional

@Controller
@RequestMapping("/oauth")
class OAuthController(
        private val oAuthService: OAuthService,
        private val userService: UserService,
        private val cookieService: CookieService
) {

    @GetMapping("/google")
    fun googleCallback(request: HttpServletRequest,
                       response: HttpServletResponse,
                       @RequestParam(required = false) code: String,
                       @RequestParam state: String) {
        if (code == null) {
            response.sendRedirect(oAuthService.getRedirectUrl(GOOGLE))
            return
        }

        val userId = oAuthService.processLogin(GOOGLE, code, state)
        val bookeyUser = userService.getOrRegister(GOOGLE, userId)
        val redirectUrl = if (bookeyUser.nickname.isNullOrEmpty()) "/" else "/register/nickname"

        configureAuthInfo(request.session, response, userId)
        response.sendRedirect(redirectUrl)
    }

    @Transactional
    fun configureAuthInfo(session: HttpSession, response: HttpServletResponse, userId: String) {
        val sessionKey = RandomStringUtils.randomAlphanumeric(32)
        session.setAttribute(sessionKey, userId)
        response.addCookie(cookieService.createAuthCookie(sessionKey))
    }
}