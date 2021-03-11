package com.bookeyproject.bookey.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Repository
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

    suspend fun fromString(url: String): Document {
        return CoroutineScope(Dispatchers.IO)
            .runCatching { URL(url) }
            .getOrThrow()
            .let { fromURL(it) }
    }
}