package com.bookeyproject.bookey.web.domain

import com.bookeyproject.bookey.web.type.ResponseType
import com.bookeyproject.bookey.web.type.ResponseType.SUCCESS
import com.fasterxml.jackson.annotation.JsonUnwrapped

data class StandardResponse<T>(
    val success: Boolean = true,
    val resultCode: String = SUCCESS.resultCode,
    val resultMessage: String = SUCCESS.resultMessage,
    @JsonUnwrapped
    val body: T? = null
) {
    constructor(responseType: ResponseType) : this(
        false,
        responseType.resultCode,
        responseType.resultMessage
    )

    constructor(responseType: ResponseType, message: String) : this(
        false,
        responseType.resultCode,
        message
    )

    constructor(body: T) : this(
        true,
        body = body
    )
}
