package com.bookeyproject.bookey.oauth.handler

import com.bookeyproject.bookey.oauth.constant.OAuthProvider
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
    private val prefixes = listOf("춤추는", "혼란스러운", "좀 치는", "수다스러운", "능숙한", "불타는", "무시무시한", "금빛", "근사한", "행복한", "고약한", "막강한", "하찮은", "못생긴", "비겁한", "화난", "발랄한", "귀여운", "꼼꼼한", "다정한", "얍삽빠른", "쿨한", "해맑은", "신선한", "차가운", "도도한", "튼튼한")
    private val postfixes = listOf("나스닥", "도도새", "복숭아", "양말", "루돌프", "반달가슴곰", "바다코끼리", "고추잠자리", "바지", "조끼", "올빼미", "승부사", "오렌지", "딸기", "망고", "감자", "고구마", "토끼", "판다곰", "병아리", "도마뱀", "케이크", "마라탕", "피자", "잠옷", "판다", "우니")

    suspend fun getUserInfo(request: ServerRequest): ServerResponse =
        request.attributeOrNull("userId")
            ?.let { it as String }
            ?.let { userRepository.findById(it) }
            ?.let { ok().bodyValueAndAwait(it) }
            ?: badRequest().bodyValueAndAwait("No user authentication information found")

    suspend fun getUserInfoWithProvider(provider: OAuthProvider, userId: String): BookeyUser? =
        when (provider) {
            OAuthProvider.GOOGLE -> userRepository.findByGoogleId(userId)
            else -> null
        }

    @ExperimentalStdlibApi
    suspend fun getRandomNickname(request: ServerRequest): ServerResponse =
        prefixes.randomOrNull()
            ?.let { prefix -> postfixes.randomOrNull()
                ?.let { postfix -> "$prefix $postfix" }
                ?: throw LoginException("Failed to get random nickname") }
            ?.let { ok().bodyValueAndAwait(it) }
            ?: throw LoginException("Failed to get random nickname")

    suspend fun setNickname(request: ServerRequest): ServerResponse {
        request.attributeOrNull("userId")
            ?.let { it as String }
            ?.let { userId ->
                userRepository.findById(userId)
                    ?.also {
                        it.nickname = request.queryParamOrNull("nickname")
                        userRepository.update(it) }
                    ?: throw InvalidDataAccessApiUsageException("No user found with userId $userId") }
            ?: throw IllegalArgumentException("No userId in request")

        return ok().bodyValueAndAwait(true)
    }
}
