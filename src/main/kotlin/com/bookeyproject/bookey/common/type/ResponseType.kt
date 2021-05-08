package com.bookeyproject.bookey.common.type

enum class ResponseType(
    val resultCode: String,
    val resultMessage: String
) {
    // General
    SUCCESS("00", "SUCCESS"),
    UNKNOWN_ERROR("10", "Dev: General failure"),
    BAD_REQUEST("11", "Check request params"),
    UNAUTHORIZED("12", "Please login"),
    NOT_FOUND("13", "Resource not found"),
    FORBIDDEN("14", "You are forbidden to use this service")
}