package com.bookeyproject.bookey.oauth.service

import com.bookeyproject.bookey.oauth.constant.OAuthProvider
import com.bookeyproject.bookey.oauth.model.BookeyUser
import com.bookeyproject.bookey.oauth.repository.UserRepository
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.stereotype.Service

@Service
class UserService(
        private val userRepository: UserRepository
){
    fun getOrRegister(provider: OAuthProvider, userId: String): BookeyUser {
        val recordList =  when (provider) {
            OAuthProvider.GOOGLE -> userRepository.findByGoogleId(userId)
            else -> throw InvalidDataAccessApiUsageException("Invalid provider type")
        }

        return if (recordList.isEmpty()) register(provider, userId) else recordList.first()
    }

    fun register(provider: OAuthProvider, userId: String): BookeyUser {
        val newRecord = when(provider) {
            OAuthProvider.GOOGLE -> BookeyUser(googleId = userId)
            OAuthProvider.NAVER -> BookeyUser(naverId = userId)
            OAuthProvider.KAKAO -> BookeyUser(kakaoId = userId)
            else -> throw InvalidDataAccessApiUsageException("Invalid provider type")
        }
        return userRepository.save(newRecord)
    }

    fun setNickname(userId: String, nickname: String) {
        userRepository.findById(userId)
                .orElseThrow()
                .copy(nickname = nickname)
                .run { userRepository.save(this) }
    }
}
