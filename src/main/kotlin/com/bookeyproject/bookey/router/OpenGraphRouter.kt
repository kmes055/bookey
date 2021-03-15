package com.bookeyproject.bookey.router

import com.bookeyproject.bookey.handler.OpenGraphHandler
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class OpenGraphRouter {
    private val log = KotlinLogging.logger { }

    @Bean
    fun openGraphRoutes(openGraphHandler: OpenGraphHandler) = coRouter {
        GET("v1.0/og/search", openGraphHandler::getOpenGraphInfo)
    }
}
