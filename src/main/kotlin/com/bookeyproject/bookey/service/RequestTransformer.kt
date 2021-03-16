package com.bookeyproject.bookey.service

interface RequestTransformer<R, M> {
    suspend fun toModel(request: R): M
}