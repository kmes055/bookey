package com.bookeyproject.bookey.repository

import com.bookeyproject.bookey.constant.OAuthProvider
import com.bookeyproject.bookey.model.BookeyUser
import org.springframework.data.jpa.domain.Specification

class UserSpecifications {
    companion object {
        fun findProviderAndId(provider: OAuthProvider, userId: String): Specification<BookeyUser> {
            return Specification { root, query, criteriaBuilder ->
                    criteriaBuilder.equal(root.get<String>(provider.columnName), userId)
            }
        }
    }

}