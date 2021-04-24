package com.bookeyproject.bookey.opengraph.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange
import java.net.URI
import java.net.URL

@Repository
class OpenGraphClient {
    companion object {
        const val META = "meta"
        const val PROPERTY = "property"
        const val CONTENT = "content"
        const val HREF = "href"
    }

    suspend fun fromURL(url: URL): Document = coroutineScope {
        url.let { CoroutineScope(Dispatchers.IO)
            .runCatching { Jsoup.parse(it, 1000) }
            .getOrThrow()
        }
    }

    suspend fun fromString(url: String): Document =
        CoroutineScope(Dispatchers.IO)
            .runCatching { URL(url) }
            .getOrThrow()
            .let { fromURL(it) }

    suspend fun isValidFaviconUrl(uri: URI): Boolean {
        return WebClient
            .builder()
            .build()
            .get()
            .uri(uri)
            .awaitExchange()
            .let { when {
                it.statusCode().is2xxSuccessful -> true
                it.statusCode().is3xxRedirection ->
                    it.headers().header("Location").let { loc -> isValidFaviconUrl(URI(loc.first())) }
                else -> false
            }}
    }

    suspend fun getFaviconFromDocument(document: Document): String {
        val candidate1 = document.head()
            ?.select("link[href~=.*\\.(ico|png)]")
            ?.first()
            ?.attr(OpenGraphClient.HREF)
        val candidate2 = document.head()
            ?.select("meta[itemprop=image]")
            ?.first()
            ?.attr(OpenGraphClient.CONTENT)

        return candidate1
            ?: candidate2
            ?: StringUtils.EMPTY
    }
}