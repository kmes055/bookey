package com.bookeyproject.bookey.web.exception

import com.bookeyproject.bookey.web.type.ResponseType

class ApiException(val responseType: ResponseType) : RuntimeException()
