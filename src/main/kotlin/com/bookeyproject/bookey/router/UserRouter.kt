package com.bookeyproject.bookey.router

import com.bookeyproject.bookey.domain.BookeyUser
import com.bookeyproject.bookey.handler.UserHandler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import mu.KotlinLogging
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.reactive.function.server.*

@Configuration
class UserRouter{
    private val log = KotlinLogging.logger {  }

    @Bean
    @RouterOperations(
        RouterOperation(path = "/auth/v1.0/user/nickname", method = [POST], beanClass = UserHandler::class, beanMethod = "setNickname"),
        RouterOperation(path = "/auth/v1.0/user", method = [GET], beanClass = UserHandler::class, beanMethod = "getUserInfo",
            operation = Operation(operationId = "getUserInfo", summary = "Get user info from session", tags = [ "Auth" ],
            parameters = [ Parameter(`in` = ParameterIn.DEFAULT, name = "id", description = "Employee Id") ],
            responses = [
                ApiResponse(responseCode = "200", description = "success", content = [ Content(schema = Schema(implementation = BookeyUser::class)) ]),
                ApiResponse(responseCode = "401", description = "Not logged in user")
            ]))
    )
    fun userRoutes(userHandler: UserHandler): RouterFunction<ServerResponse> =
            coRouter {
                "/auth/v1.0/user".nest {
                    GET("/", userHandler::getUserInfo)
                    POST("/nickname", userHandler::setNickname)
                }
                filter { request, next ->
                    log.debug("userId in attr: {}", request.attribute("userId"))
                    request.attribute("userId").map { it as String }.orElse(null)
                        ?.takeIf { it.isNotBlank() }
                        ?.let { next(request) }
                        ?: status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait("Please login")
                }
            }

}