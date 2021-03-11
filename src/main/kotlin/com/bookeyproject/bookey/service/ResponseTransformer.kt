package com.bookeyproject.bookey.service

interface ResponseTransformer<M, R> {
    suspend fun fromModel(model: M): R
}