package com.bookeyproject.bookey.handler

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.*

@Service
class MonitorHandler {
    private val log = KotlinLogging.logger{}
    private var status: Boolean = true

    suspend fun healthCheck(request: ServerRequest): ServerResponse =
            if (status) ok().bodyValueAndAwait("ok") else status(HttpStatus.SERVICE_UNAVAILABLE).buildAndAwait()

    suspend fun enable(request: ServerRequest): ServerResponse {
        this.status = true
        log.info("Health check status set to {}", status)

        return ok().buildAndAwait()
    }

    suspend fun disable(request: ServerRequest): ServerResponse {
        this.status = false
        log.info("Health check status set to {}", status)

        return ok().buildAndAwait()
    }

    suspend fun dummy(request: ServerRequest): ServerResponse {
        return ok().bodyValueAndAwait(request.awaitSession().attributes)
    }
}