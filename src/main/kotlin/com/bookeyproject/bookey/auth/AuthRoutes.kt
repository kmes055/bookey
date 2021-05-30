package com.bookeyproject.bookey.auth

import com.bookeyproject.bookey.auth.exception.LoginException
import com.bookeyproject.bookey.auth.handler.OAuthHandler
import com.bookeyproject.bookey.auth.handler.UserHandler
import com.bookeyproject.bookey.web.domain.StandardResponse
import com.bookeyproject.bookey.web.type.ResponseType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AuthRoutes {

    @ExperimentalStdlibApi
    @Bean
    fun userRoutes(userHandler: UserHandler) = coRouter {
        "/user".nest {
            GET("/", userHandler::getUserInfo)
            GET("/nickname", userHandler::getRandomNickname)
            POST("/nickname", userHandler::setNickname)
            filter(::mockingLoginFilter)
        }

        onError<LoginException> { e, _ ->
            ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.UNAUTHORIZED, e.message ?: ""))
        }
        onError<Throwable> { e, _ ->
            ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.UNKNOWN_ERROR, e.message ?: ""))
        }
    }

    @Bean
    fun oauthRoutes(oAuthHandler: OAuthHandler) = coRouter {

        GET("/oauth/{provider}", oAuthHandler::redirect)
        GET("/callback/{provider}", oAuthHandler::handleCallback)
        filter(::notLoginFilter)

    }
}
