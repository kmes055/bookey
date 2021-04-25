package com.bookeyproject.bookey.opengraph.client

import com.bookeyproject.bookey.opengraph.constant.OpenGraphConstant.CONTENT
import com.bookeyproject.bookey.opengraph.constant.OpenGraphConstant.HREF
import com.bookeyproject.bookey.opengraph.constant.OpenGraphConstant.USER_AGENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Repository
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange
import java.net.URI

@Repository
class OpenGraphClient {

    suspend fun fetch(url: String): Document =
        CoroutineScope(Dispatchers.IO)
            .runCatching {
                Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .get()
            }
            .getOrThrow()


    suspend fun isValidFaviconUrl(uri: URI): Boolean {
        return WebClient
            .builder()
            .build()
            .get()
            .uri(uri)
            .awaitExchange()
            .let {
                when {
                    it.statusCode().is2xxSuccessful -> true
                    it.statusCode().is3xxRedirection ->
                        it.headers().header("Location").let { loc -> isValidFaviconUrl(URI(loc.first())) }
                    else -> false
                }
            }
    }

    suspend fun getFaviconFromDocument(document: Document): String {
        val candidate1 = document.head()
            ?.select("link[href~=.*\\.(ico|png)]")
            ?.first()
            ?.attr(HREF)
        val candidate2 = document.head()
            ?.select("meta[itemprop=image]")
            ?.first()
            ?.attr(CONTENT)

        return candidate1
            ?: candidate2
            ?: StringUtils.EMPTY
    }



}