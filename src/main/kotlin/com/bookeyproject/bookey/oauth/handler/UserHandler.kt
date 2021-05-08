package com.bookeyproject.bookey.oauth.handler

import com.bookeyproject.bookey.common.domain.StandardResponse
import com.bookeyproject.bookey.common.type.ResponseType
import com.bookeyproject.bookey.common.util.RouterUtils
import com.bookeyproject.bookey.oauth.constant.OAuthProvider
import com.bookeyproject.bookey.oauth.constant.UserNickname
import com.bookeyproject.bookey.oauth.domain.BookeyUser
import com.bookeyproject.bookey.oauth.exception.LoginException
import com.bookeyproject.bookey.oauth.repository.UserRepository
import mu.KotlinLogging
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Service
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
            ?: RouterUtils.redirectLogin()

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
