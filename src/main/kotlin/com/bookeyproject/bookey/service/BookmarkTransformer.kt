package com.bookeyproject.bookey.service

import com.bookeyproject.bookey.client.OpenGraphClient
import com.bookeyproject.bookey.domain.Bookmark
import com.bookeyproject.bookey.domain.BookmarkRequest
import com.bookeyproject.bookey.domain.BookmarkResponse
import com.bookeyproject.bookey.repository.BookmarkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils.EMPTY
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange
import java.net.URI
import java.net.URL

@Service
class BookmarkTransformer(
    private val bookmarkRepository: BookmarkRepository,
    private val openGraphClient: OpenGraphClient
) : RequestTransformer<BookmarkRequest, Bookmark>, ResponseTransformer<Bookmark, BookmarkResponse> {
    private val log = KotlinLogging.logger { }

    override suspend fun toModel(request: BookmarkRequest): Bookmark =
        Bookmark().apply {
            this.id = generateBookmarkId()
            this.url = request.url
            this.directory = request.directory ?: EMPTY
            this.memo = request.memo ?: EMPTY
            this.ownerId = request.ownerId
        }.also { it.setOpenGraph() }


    override suspend fun fromModel(model: Bookmark): BookmarkResponse = BookmarkResponse(
        model.id,
        model.title,
        model.description,
        model.url,
        model.directory,
        model.siteName,
        model.favicon,
        model.thumbnail,
        model.memo,
        model.createdAt,
        model.clickCount
    )

    suspend fun updateBookmark(model: Bookmark, req: BookmarkRequest) = model.run {
        url = req.url
        memo = req.memo ?: EMPTY
        directory = req.directory ?: directory

        setOpenGraph()
    }

    private suspend fun generateBookmarkId(): String =
        RandomStringUtils.randomAlphanumeric(16)
            .let {
                bookmarkRepository.findById(it)
                    ?.let { generateBookmarkId() }
                    ?: it
            }

    private suspend fun Bookmark.setOpenGraph() {
        val document = openGraphClient.fromString(url)

        favicon = URI(url)
            .let { URI("${it.scheme}://${it.host}/favicon.ico") }
            .takeIf { isValidFaviconUrl(it) }
            ?.toString()
            ?: getFaviconFromDocument(document)

        document.getElementsByTag(OpenGraphClient.META)
            .let { it.filter { element -> element.hasAttr(OpenGraphClient.PROPERTY) } }
            .forEach {
                val content = it.attr(OpenGraphClient.CONTENT)
                log.debug("Content: {}", content)
                when (it.attr(OpenGraphClient.PROPERTY)) {
                    "og:title" -> this.title = content
                    "og:description" -> this.description = content
                    "og:image" -> this.thumbnail = content
                    "og:type" -> this.type = content
                    "og:site_name" -> this.siteName = content
                }
            }
    }

    private suspend fun isValidFaviconUrl(uri: URI): Boolean {
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

    private suspend fun getFaviconFromDocument(document: Document): String {
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
            ?: EMPTY
    }
}