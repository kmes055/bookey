package com.bookeyproject.bookey.oauth.controller

import com.bookeyproject.bookey.oauth.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v1.0/user")
class UserController(
        private val userService: UserService
) {
    @PostMapping("/nickname")
    fun setNickname(request: HttpServletRequest, @RequestParam nickname: String): Boolean {
        return request.getAttribute("userId")
                ?.let { userService.setNickname(it as String, nickname) }
                ?.let { true }
                ?: false
    }
}