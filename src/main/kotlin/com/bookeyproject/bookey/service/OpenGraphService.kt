package com.bookeyproject.bookey.service

import com.bookeyproject.bookey.client.OpenGraphClient
import com.bookeyproject.bookey.domain.OpenGraphInfo
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.net.URI

@Service
class OpenGraphService(
    private val openGraphClient: OpenGraphClient
) {
    private val log = KotlinLogging.logger { }

    suspend fun getOpenGraphInfo(url: String): OpenGraphInfo =
        OpenGraphInfo().apply {
            openGraphClient.fromString(url)
                .getElementsByTag(OpenGraphClient.META)
                .filter { element -> element.hasAttr(OpenGraphClient.PROPERTY) }
                .forEach {
                    val content = it.attr(OpenGraphClient.CONTENT)
                    log.debug("Content: {}", content)
                    when (it.attr(OpenGraphClient.PROPERTY)) {
                        "og:title" -> title = content
                        "og:description" -> description = content
                        "og:image" -> image = content
                        "og:type" -> type = content
                        "og:site_name" -> siteName = content
                    }
                }
        }.apply { favicon = getFavicon(url) }

    suspend fun getFavicon(url: String): String =
        URI(url)
            .let { URI("${it.scheme}://${it.host}/favicon.ico") }
            .takeIf { openGraphClient.isValidFaviconUrl(it) }
            ?.toString()
            ?: openGraphClient.getFaviconFromDocument(openGraphClient.fromString(url))
}