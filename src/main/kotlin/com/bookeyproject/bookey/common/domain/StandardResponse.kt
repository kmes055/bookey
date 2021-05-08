package com.bookeyproject.bookey.common.domain

import com.bookeyproject.bookey.common.type.ResponseType

data class StandardResponse<T>(
    val success: Boolean = true,
    val resultCode: String = "00",
    val resultMessage: String = "Success",
    val body: T?
) {
    constructor(responseType: ResponseType): this(
        false,
        responseType.resultCode,
        responseType.resultMessage,
        null
    )

    constructor(responseType: ResponseType, message: String): this(
        false,
        responseType.resultCode,
        message,
        null
    )

    constructor(body: T): this(
        true,
        "00",
        "Success",
        body
    )
}
