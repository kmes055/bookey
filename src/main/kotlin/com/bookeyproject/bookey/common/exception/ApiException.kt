package com.bookeyproject.bookey.common.exception

import com.bookeyproject.bookey.common.type.ResponseType

class ApiException(val responseType: ResponseType) : RuntimeException()
