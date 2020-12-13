package com.bookeyproject.bookey.router

import com.bookeyproject.bookey.handler.BookmarkHandler
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
@RequestMapping("/v1.0/bookmarks")
class BookmarkRouter{
    private val log = KotlinLogging.logger {  }

    @Bean
    fun bookmarkRoutes(bookmarkHandler: BookmarkHandler) = coRouter {
        "/v1.0/bookmarks".nest {
            GET("/", bookmarkHandler::getBookmarks)
            GET("/{id}", bookmarkHandler::getBookmark)
            POST("/", bookmarkHandler::addBookmark)
            PUT("/", bookmarkHandler::modifyBookmark)
        }
        filter { request, next ->
            request.attribute("userId").map { it as String }.orElse(null)
                ?.takeIf { it.isNotBlank() }
                ?.let { next(request) }
                ?: status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait("Please login")
        }
    }
}
