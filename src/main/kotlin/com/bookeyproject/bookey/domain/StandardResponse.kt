package com.bookeyproject.bookey.domain

data class StandardResponse<T>(
    private var header: StandardResponseHeader,
    private var body: T? = null
)