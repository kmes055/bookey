package com.bookeyproject.bookey.oauth.router

import com.bookeyproject.bookey.oauth.domain.BookeyUser
import com.bookeyproject.bookey.oauth.handler.UserHandler
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
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter {
    private val log = KotlinLogging.logger { }

    @ExperimentalStdlibApi
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/user/nickname",
            method = [POST],
            beanClass = UserHandler::class,
            beanMethod = "setNickname"
        ),
        RouterOperation(
            path = "/user", method = [GET], beanClass = UserHandler::class, beanMethod = "getUserInfo",
            operation = Operation(
                operationId = "getUserInfo", summary = "Get user info from session", tags = ["Auth"],
                parameters = [Parameter(`in` = ParameterIn.DEFAULT, name = "id", description = "Employee Id")],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "success",
                        content = [Content(schema = Schema(implementation = BookeyUser::class))]
                    ),
                    ApiResponse(responseCode = "401", description = "Not logged in user")
                ]
            )
        )
    )
    fun userRoutes(userHandler: UserHandler): RouterFunction<ServerResponse> = coRouter {
        "/user".nest {
            GET("/", userHandler::getUserInfo)
            GET("/nickname/random", userHandler::getRandomNickname)
            POST("/nickname", userHandler::setNickname)
        }
        filter { request, next ->
            request.headers()
                .header("X-Bookey-id")
                .firstOrNull()
                ?.also { log.debug("userId: {}", it) }
                ?.also { request.attributes()["userId"] = it }
                ?.takeIf { it.isNotBlank() }
                ?.let { next(request) }
                ?: status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait("Please login")
        }
    }
}
