package com.bookeyproject.bookey.router

import com.bookeyproject.bookey.handler.BookmarkHandler
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.*

@Configuration
class BookmarkApiRouterV1_0 {
    private val log = KotlinLogging.logger {  }

    @Bean
    fun bookmarkRoutes(bookmarkHandler: BookmarkHandler) = coRouter {
        "v1.0/bookmarks".nest {
            GET("/", bookmarkHandler::getBookmarks)
            GET("/search", bookmarkHandler::getOpenGraphInfo)
            GET("/{id}", bookmarkHandler::getBookmark)
            POST("/", bookmarkHandler::addBookmark)
            PUT("/", bookmarkHandler::modifyBookmark)
        }
        filter { request, next ->
            request.attributes().putIfAbsent("userId", "9351c5aa-3308-40ed-8766-91f32330d314")
            next(request)
//            request.attributeOrNull("userId")
//                ?.let { it as String }
//                ?.takeIf { it.isNotBlank() }
//                ?.let { next(request) }
//                ?: status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait("Please login")
        }
    }
}
