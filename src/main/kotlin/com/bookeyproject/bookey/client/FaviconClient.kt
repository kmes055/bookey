package com.bookeyproject.bookey.client

import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange

class FaviconClient {
    suspend fun isValidFaviconUrl(url: String): Boolean {
        return WebClient
            .builder()
            .build()
            .get()
            .uri(url)
            .awaitExchange()
            .statusCode()
            .is2xxSuccessful
    }
}