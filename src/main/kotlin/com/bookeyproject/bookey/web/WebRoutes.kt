package com.bookeyproject.bookey.web

import com.bookeyproject.bookey.auth.mockingLoginFilter
import com.bookeyproject.bookey.web.domain.StandardResponse
import com.bookeyproject.bookey.web.exception.ApiException
import com.bookeyproject.bookey.web.handler.BookmarkHandler
import com.bookeyproject.bookey.web.handler.DirectoryHandler
import com.bookeyproject.bookey.web.handler.MonitorHandler
import com.bookeyproject.bookey.web.type.ResponseType
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.*

private val log = KotlinLogging.logger {}

@Configuration
class WebRoutes {
    @Bean
    fun apiRoutes(bookmarkHandler: BookmarkHandler, directoryHandler: DirectoryHandler) = coRouter {
        "v1.0".nest {
            "/bookmarks".nest {
                GET("/", bookmarkHandler::getBookmarks)
                GET("/{id}", bookmarkHandler::getBookmark)
                POST("/", bookmarkHandler::addBookmark)
                PUT("/", bookmarkHandler::modifyBookmark)
            }
            "/directories".nest {
                GET("/", directoryHandler::getDirectories)
                GET("{id}", directoryHandler::getDirectory)
                POST("/", directoryHandler::addDirectory)
            }
            filter(::mockingLoginFilter)
            onError<ApiException> { e, _ ->
                ok().bodyValueAndAwait(StandardResponse<Unit>((e as ApiException).responseType))
            }
            onError<Throwable> { e, _ ->
                ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.UNKNOWN_ERROR, e.message ?: ""))
            }
        }
    }

    @Bean
    fun monitorRoutes(monitorHandler: MonitorHandler) = coRouter {
        GET("/monitor/l7check", monitorHandler::healthCheck)

        "/monitor".nest {
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