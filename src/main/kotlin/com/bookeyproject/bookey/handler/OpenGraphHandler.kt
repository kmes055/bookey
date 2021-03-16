package com.bookeyproject.bookey.handler

import com.bookeyproject.bookey.service.OpenGraphService
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Service
class OpenGraphHandler(
    private val openGraphService: OpenGraphService
) {

    suspend fun getOpenGraphInfo(request: ServerRequest): ServerResponse =
        request.queryParamOrNull("url")
            ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }
            ?.let { openGraphService.getOpenGraphInfo(it) }
            ?.let { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(it) }
            ?: ServerResponse.notFound().buildAndAwait()
}