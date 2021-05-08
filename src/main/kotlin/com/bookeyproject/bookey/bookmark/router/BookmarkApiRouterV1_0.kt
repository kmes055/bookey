package com.bookeyproject.bookey.bookmark.router

import com.bookeyproject.bookey.bookmark.handler.BookmarkHandler
import com.bookeyproject.bookey.common.domain.StandardResponse
import com.bookeyproject.bookey.common.exception.ApiException
import com.bookeyproject.bookey.common.type.ResponseType
import com.bookeyproject.bookey.common.util.RouterUtils
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class BookmarkApiRouterV1_0 {
    private val log = KotlinLogging.logger { }

    @Bean
    fun bookmarkRoutes(bookmarkHandler: BookmarkHandler) = coRouter {
        "v1.0/bookmarks".nest {
            GET("/", bookmarkHandler::getBookmarks)
            GET("/{id}", bookmarkHandler::getBookmark)
            POST("/", bookmarkHandler::addBookmark)
            PUT("/", bookmarkHandler::modifyBookmark)
        }
        filter(RouterUtils.Companion::mockingLoginFilter)
        onError<ApiException> { e, _ ->
            ok().bodyValueAndAwait(StandardResponse<Unit>((e as ApiException).responseType))
        }
        onError<Throwable> { e, _ ->
            ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.UNKNOWN_ERROR, e.message ?: ""))
        }
    }
}
