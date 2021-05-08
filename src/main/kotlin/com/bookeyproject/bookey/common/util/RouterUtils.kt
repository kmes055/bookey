package com.bookeyproject.bookey.common.util

import kotlinx.coroutines.reactive.awaitFirst
import mu.KotlinLogging
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok

class RouterUtils {
    companion object {
        private val log = KotlinLogging.logger {}
        private const val visitHeader = "X-bky-his"

        suspend fun loginFilter(request: ServerRequest, next: suspend (ServerRequest) -> ServerResponse): ServerResponse =
            getHeader(request)
                ?.also { log.debug("userId: {}", it) }
                ?.also { request.attributes()["userId"] = it }
                ?.takeIf { it.isNotBlank() }
                ?.let { next(request) }
                ?: if (request.hasVisitHistory()) redirectLanding() else redirectLogin()

        suspend fun notLoginFilter(request: ServerRequest, next: suspend (ServerRequest) -> ServerResponse): ServerResponse =
            getHeader(request)
                ?.let { redirectMain() }
                ?: next(request)

        private fun getHeader(request: ServerRequest): String? =
            request.headers()
                .header("X-Bookey-id")
                .firstOrNull()

        suspend fun mockingLoginFilter(request: ServerRequest, next: suspend (ServerRequest) -> ServerResponse): ServerResponse =
            request.attributes().putIfAbsent("userId", "9351c5aa-3308-40ed-8766-91f32330d314")
                .let { next(request) }

        suspend fun redirectLogin(): ServerResponse = redirectTo("/login")

        suspend fun redirectMain(): ServerResponse = redirectTo("/bookmark")

        suspend fun redirectLanding(): ServerResponse = redirectTo("/")

        private suspend fun redirectTo(path: String): ServerResponse = ok().render("redirect:${path}").awaitFirst()

        private fun ServerRequest.hasVisitHistory(): Boolean =
            this.headers()
                .firstHeader(visitHeader)
                ?.isNotBlank()
                ?: false
    }
}