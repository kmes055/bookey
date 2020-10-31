package com.bookeyproject.bookey.service

import com.bookeyproject.bookey.constant.OAuthProvider
import com.bookeyproject.bookey.model.BookeyUser
import com.bookeyproject.bookey.repository.UserRepository
import com.bookeyproject.bookey.repository.UserSpecifications
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class UserService(
        private val userRepository: UserRepository
){
    fun getOrRegister(provider: OAuthProvider, userId: String): BookeyUser {

        return userRepository.findAll(Specification.where(UserSpecifications.findProviderAndId(provider, userId)))
                .first()
    }

    fun setNickname(userId: String, nickname: String) {
        userRepository.findById(userId)
                .orElseThrow()
                .copy(nickname = nickname)
                .run { userRepository.save(this) }
    }
}