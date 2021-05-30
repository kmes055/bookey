package com.bookeyproject.bookey.web.service

interface ResponseTransformer<M, R> {
    suspend fun fromModel(model: M): R
}