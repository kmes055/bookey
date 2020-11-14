package com.bookeyproject.bookey.oauth.controller

import com.bookeyproject.bookey.oauth.constant.OAuthProvider
import com.bookeyproject.bookey.oauth.constant.OAuthProvider.GOOGLE
import com.bookeyproject.bookey.oauth.constant.OAuthProvider.NAVER
import com.bookeyproject.bookey.oauth.service.CookieService
import com.bookeyproject.bookey.oauth.service.OAuthService
import com.bookeyproject.bookey.oauth.service.UserService
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.transaction.Transactional

@RestController
@RequestMapping("/oauth")
class OAuthController(
        private val oAuthService: OAuthService,
        private val userService: UserService,
        private val cookieService: CookieService
) {
    private val log = KotlinLogging.logger {}

    @GetMapping("/google")
    fun googleCallback(request: HttpServletRequest,
                       response: HttpServletResponse,
                       @RequestParam(required = false) code: String?,
                       @RequestParam(required = false) state: String?) {
        oAuthCallback(request, response, code, state, GOOGLE)
    }

    @GetMapping("/naver")
    fun naverCallback(request: HttpServletRequest,
                      response: HttpServletResponse,
                      @RequestParam(required = false) code: String?,
                      @RequestParam(required = false) state: String?) {
        oAuthCallback(request, response, code, state, NAVER)
    }

    private fun oAuthCallback(request: HttpServletRequest,
                              response: HttpServletResponse,
                              code: String?,
                              state: String?,
                              provider: OAuthProvider
    ) {
        log.debug("Code: {}, state: {}", code, state)
        if (code == null || state == null) {
            response.sendRedirect(oAuthService.getRedirectUrl(provider))
            return
        }

        oAuthService.processLogin(provider, code, state)
                .let { userService.getOrRegister(provider, it) }
                .apply { configureAuthInfo(request.session, response, this.userId) }
                .let { if (it.nickname.isNullOrEmpty()) "/register/nickname" else "/" }
                .let { response.sendRedirect(it) }
    }

    @Transactional
    fun configureAuthInfo(session: HttpSession, response: HttpServletResponse, userId: String) {
        val sessionKey = RandomStringUtils.randomAlphanumeric(32)
        session.setAttribute(sessionKey, userId)
        response.addCookie(cookieService.createAuthCookie(sessionKey))
    }

}