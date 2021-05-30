package com.bookeyproject.bookey.web.service

import com.bookeyproject.bookey.opengraph.service.OpenGraphService
import com.bookeyproject.bookey.web.domain.model.Bookmark
import com.bookeyproject.bookey.web.domain.request.BookmarkRequest
import com.bookeyproject.bookey.web.domain.response.BookmarkResponse
import com.bookeyproject.bookey.web.exception.ApiException
import com.bookeyproject.bookey.web.repository.BookmarkRepository
import com.bookeyproject.bookey.web.type.ResponseType
import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils.EMPTY
import org.springframework.stereotype.Service

@Service
class BookmarkTransformer(
    private val bookmarkRepository: BookmarkRepository,
    private val openGraphService: OpenGraphService
) : RequestTransformer<BookmarkRequest, Bookmark>, ResponseTransformer<Bookmark, BookmarkResponse> {
    private val log = KotlinLogging.logger { }

    override suspend fun toModel(request: BookmarkRequest): Bookmark =
        Bookmark().apply {
            this.id = generateBookmarkId()
            this.url = request.url
            this.directory = request.directory ?: EMPTY
            this.memo = request.memo ?: EMPTY
            this.ownerId = request.ownerId ?: throw ApiException(ResponseType.UNAUTHORIZED)
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

    suspend fun toModifiedModel(model: Bookmark, req: BookmarkRequest) = model.run {
        url = req.url
        memo = req.memo ?: EMPTY
        directory = req.directory ?: directory

        setOpenGraph()
    }

    private suspend fun Bookmark.setOpenGraph() =
        openGraphService.getOpenGraphInfo(url)
            .also {
                title = title.ifBlank { it.title }
                description = description.ifBlank { it.title }
                thumbnail = thumbnail.ifBlank { it.thumbnail }
                type = type.ifBlank { it.type }
                siteName = siteName.ifBlank { it.siteName }
                favicon = favicon.ifBlank { it.favicon }
            }

    private suspend fun generateBookmarkId(): String =
        RandomStringUtils.randomAlphanumeric(16)
            .let {
                bookmarkRepository.findById(it)
                    ?.let { generateBookmarkId() }
                    ?: it
            }
}