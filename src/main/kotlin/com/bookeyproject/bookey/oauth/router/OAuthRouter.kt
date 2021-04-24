package com.bookeyproject.bookey.oauth.router

import com.bookeyproject.bookey.oauth.handler.OAuthHandler
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class OAuthRouter {
    private val log = KotlinLogging.logger {}

    @Bean
    fun oAuthRoutes(oAuthHandler: OAuthHandler): RouterFunction<ServerResponse> = coRouter {
        GET("/oauth/{provider}", oAuthHandler::redirect)
        GET("/callback/{provider}", oAuthHandler::handleCallback)
    }

}
