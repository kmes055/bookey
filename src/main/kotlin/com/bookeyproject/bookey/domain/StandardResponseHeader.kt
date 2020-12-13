package com.bookeyproject.bookey.domain

data class StandardResponseHeader(
    private var code: Int = 0,
    private var message: String = "Success",
    private var isSuccessful: Boolean = true
)