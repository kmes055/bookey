package com.bookeyproject.bookey.web

fun makeQueryString(params: Map<String, String>): String {
    return "?" +
        params.map { entry -> entry.key + "=" + entry.value }
            .joinToString("&")
}