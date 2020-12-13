package com.bookeyproject.bookey.router

import com.bookeyproject.bookey.handler.MonitorHandler
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.remoteAddressOrNull

@Configuration
class MonitorRouter {
    private val log = KotlinLogging.logger {}

    @Bean
    fun monitorRoutes(monitorHandler: MonitorHandler) =
        coRouter {
            GET("/monitor/l7check", monitorHandler::healthCheck)

            "/monitor".nest {
                GET("/dummy", monitorHandler::dummy)
                POST("/enable", monitorHandler::enable)
                POST("/disable", monitorHandler::disable)
                filter { request, next ->
                    request.remoteAddressOrNull()
                        ?.address
                        ?.takeUnless { it.isLoopbackAddress || it.isAnyLocalAddress }
                        ?.let {
                            log.info("Blocked bad request from: {}", it)
                            ServerResponse.status(HttpStatus.UNAUTHORIZED).buildAndAwait()
                        }
                        ?: next(request)
                }
            }
        }

}