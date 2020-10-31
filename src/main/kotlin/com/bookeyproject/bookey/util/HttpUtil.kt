package com.bookeyproject.bookey.util

class HttpUtil {
    companion object {
        fun makeQueryString(params: Map<String, String>): String {
            return "?" +
                    params.map { entry -> entry.key + "=" + entry.value }
                            .joinToString("&")
        }
    }
}