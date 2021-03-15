package com.bookeyproject.bookey.service

import com.bookeyproject.bookey.domain.Bookmark
import com.bookeyproject.bookey.domain.BookmarkRequest
import com.bookeyproject.bookey.domain.BookmarkResponse
import com.bookeyproject.bookey.repository.BookmarkRepository
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

    private suspend fun Bookmark.setOpenGraph() =
        openGraphService.getOpenGraphInfo(url)
            .also {
                this.title = it.title
                this.description = it.description
                this.thumbnail = it.image
                this.type = it.type
                this.siteName = it.siteName
                this.favicon = it.favicon
            }

    private suspend fun generateBookmarkId(): String =
        RandomStringUtils.randomAlphanumeric(16)
            .let {
                bookmarkRepository.findById(it)
                    ?.let { generateBookmarkId() }
                    ?: it
            }

}