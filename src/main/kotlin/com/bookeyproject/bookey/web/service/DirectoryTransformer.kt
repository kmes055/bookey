package com.bookeyproject.bookey.web.service

import com.bookeyproject.bookey.web.domain.model.Directory
import com.bookeyproject.bookey.web.domain.request.DirectoryRequest
import com.bookeyproject.bookey.web.domain.response.DirectoryResponse
import com.bookeyproject.bookey.web.exception.ApiException
import com.bookeyproject.bookey.web.repository.DirectoryRepository
import com.bookeyproject.bookey.web.type.ResponseType
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Service
import org.apache.commons.lang3.StringUtils.EMPTY
import java.time.OffsetDateTime

@Service
class DirectoryTransformer(private val directoryRepository: DirectoryRepository):
    RequestTransformer<DirectoryRequest, Directory>, ResponseTransformer<Directory, DirectoryResponse> {
    override suspend fun toModel(request: DirectoryRequest): Directory =
        Directory(
            generateDirectoryId(),
            request.name,
            request.parent ?: EMPTY,
            request.userId ?: throw ApiException(ResponseType.UNAUTHORIZED),
            OffsetDateTime.now()
        )

    override suspend fun fromModel(model: Directory): DirectoryResponse =
        DirectoryResponse(model)

    private suspend fun generateDirectoryId(): String =
        RandomStringUtils.randomAlphanumeric(16)
            .let {
                directoryRepository.findById(it)
                    ?.let { generateDirectoryId() }
                    ?: it
            }
}