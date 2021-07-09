package com.bookeyproject.bookey.web.handler

import com.bookeyproject.bookey.web.domain.StandardResponse
import com.bookeyproject.bookey.web.domain.request.DirectoryRequest
import com.bookeyproject.bookey.web.repository.DirectoryRepository
import com.bookeyproject.bookey.web.service.DirectoryTransformer
import com.bookeyproject.bookey.web.type.ResponseType
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class DirectoryHandler(
    private val directoryRepository: DirectoryRepository,
    private val directoryTransformer: DirectoryTransformer
) {
    private val logger = KotlinLogging.logger{}

    suspend fun getDirectories(request: ServerRequest): ServerResponse =
        request.attributeOrNull("userId")
            ?.let { directoryRepository.findAllByUserId(it as String) }
            ?.map { directoryTransformer.fromModel(it) }
            ?.let { StandardResponse(it.toList()) }
            ?.let { ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(it) }
            ?: ServerResponse.ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.NOT_FOUND))

    suspend fun getDirectory(request: ServerRequest): ServerResponse =
        request.pathVariable("id")
            .let { directoryRepository.findOne(it) }
            ?.let { directoryTransformer.fromModel(it) }
            ?.let { StandardResponse(it) }
            ?.let { ServerResponse.ok().bodyValueAndAwait(it) }
            ?: ServerResponse.ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.NOT_FOUND))

    suspend fun addDirectory(request: ServerRequest): ServerResponse =
        request.awaitBodyOrNull<DirectoryRequest>()
            ?.also { it.userId = request.attributeOrNull("userId") as String }
            ?.let { directoryTransformer.toModel(it) }
            ?.also { directoryRepository.save(it) }
            ?.let { directoryTransformer.fromModel(it) }
            ?.let { StandardResponse(it) }
            ?.let { ServerResponse.ok().bodyValueAndAwait(it) }
            ?: ServerResponse.ok().bodyValueAndAwait(StandardResponse<Unit>(ResponseType.UNKNOWN_ERROR))

}