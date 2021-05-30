package com.bookeyproject.bookey.auth.handler

import com.bookeyproject.bookey.auth.constant.UserNickname
import com.bookeyproject.bookey.auth.exception.LoginException
import com.bookeyproject.bookey.auth.redirectLogin
import com.bookeyproject.bookey.auth.repository.UserRepository
import com.bookeyproject.bookey.web.domain.StandardResponse
import com.bookeyproject.bookey.web.type.ResponseType
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class UserHandler(
    private val userRepository: UserRepository
) {
    private val log = KotlinLogging.logger {}

    suspend fun getUserInfo(request: ServerRequest): ServerResponse =
        request.attributeOrNull("userId")
            ?.let { it as String }
            ?.let { userRepository.findById(it) }
            ?.let { StandardResponse(it) }
            ?.let { ok().bodyValueAndAwait(it) }
            ?: redirectLogin()

    @ExperimentalStdlibApi
    suspend fun getRandomNickname(request: ServerRequest): ServerResponse =
        "${UserNickname.prefixes.randomOrNull()} ${UserNickname.postfixes.randomOrNull()}"
            .takeUnless { it.contains("null") }
            ?.let { StandardResponse(it) }
            ?.let { ok().bodyValueAndAwait(it) }
            ?: throw LoginException("Failed to get random nickname")

    suspend fun setNickname(request: ServerRequest): ServerResponse =
        request.attributeOrNull("userId")
            ?.let { it as String }
            ?.let { userRepository.findById(it) }
            ?.also { it.nickname = request.queryParamOrNull("nickname") }
            ?.also { userRepository.save(it) }
            ?.let { ok().bodyValueAndAwait(StandardResponse<Unit>()) }
            ?: ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.NOT_FOUND))
}
