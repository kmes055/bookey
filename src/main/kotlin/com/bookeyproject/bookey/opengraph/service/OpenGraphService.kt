package com.bookeyproject.bookey.opengraph.service

import com.bookeyproject.bookey.opengraph.client.OpenGraphClient
import com.bookeyproject.bookey.opengraph.constant.OpenGraphConstant.CONTENT
import com.bookeyproject.bookey.opengraph.constant.OpenGraphConstant.META
import com.bookeyproject.bookey.opengraph.constant.OpenGraphConstant.NAME
import com.bookeyproject.bookey.opengraph.constant.OpenGraphConstant.PROPERTY
import com.bookeyproject.bookey.opengraph.domain.OpenGraphInfo
import com.bookeyproject.bookey.web.domain.StandardResponse
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.net.URI

@Service
class OpenGraphService(
    private val openGraphClient: OpenGraphClient
) {
    private val log = KotlinLogging.logger { }

    suspend fun getOpenGraphInfo(url: String): StandardResponse<OpenGraphInfo> =
        StandardResponse(fetchOpenGraphInfo(url))

    suspend fun fetchOpenGraphInfo(url: String) =
        OpenGraphInfo().apply {
            val document = openGraphClient.fetch(url.schemed())

            document.getElementsByTag(META).forEach {
                val content = it.attr(CONTENT)
                val key = if (it.hasAttr(PROPERTY)) it.attr(PROPERTY) else it.attr(NAME)
                log.debug("Lookup $key $content")
                when (key) {
                    "og:title" -> title = content
                    "og:description" -> description = content
                    "og:image" -> thumbnail = content
                    "og:type" -> type = content
                    "og:site_name" -> siteName = content
                    "twitter:title" -> title = content
                    "twitter:image" -> thumbnail = content
                    "twitter:description" -> description = content
                    "twitter:site" -> siteName = content
                    "twitter:creator" -> creator = content
                    "twitter:card" -> type = content
                }
            }

            if (siteName.isBlank()) siteName = url.extractTitle()
            if (title.isBlank()) title = document.title()
            favicon = getFavicon(url.schemed())
        }

    suspend fun getFavicon(url: String): String =
        URI(url)
            .let { URI("${it.scheme}://${it.host}/favicon.ico") }
            .takeIf { openGraphClient.isValidFaviconUrl(it) }
            ?.toString()
            ?: openGraphClient.getFaviconFromDocument(openGraphClient.fetch(url))

    suspend fun String.schemed() =
        if (!contains("://")) "http://$this"
        else if (startsWith("://")) "http$this"
        else this

    suspend fun String.extractTitle(): String =
        URI(schemed())
            .host
            .split(".")
            .let { it[it.lastIndex - 1] }
}