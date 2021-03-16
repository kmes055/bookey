package com.bookeyproject.bookey.handler

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import java.net.URL

@Service
class MonitorHandler {
    private val log = KotlinLogging.logger {}
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

    suspend fun dummy(request: ServerRequest): ServerResponse = coroutineScope {
       ok().bodyValueAndAwait(42)
    }
}