package com.bookeyproject.bookey.web.service

interface RequestTransformer<R, M> {
    suspend fun toModel(request: R): M
}