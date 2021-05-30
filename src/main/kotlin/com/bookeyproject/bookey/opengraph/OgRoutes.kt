package com.bookeyproject.bookey.opengraph

import com.bookeyproject.bookey.opengraph.handler.OpenGraphHandler
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

private val log = KotlinLogging.logger { }

@Configuration
class Routes(private val openGraphHandler: OpenGraphHandler) {
    @Bean
    fun openGraphRoutes() = coRouter {
        GET("v1.0/og/search", openGraphHandler::getOpenGraphInfo)
    }
}