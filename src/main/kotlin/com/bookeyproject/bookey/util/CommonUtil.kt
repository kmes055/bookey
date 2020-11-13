package com.bookeyproject.bookey.util

import org.slf4j.LoggerFactory

class CommonUtil {
    companion object {
        inline fun <reified T : Any> loggerFor() = LoggerFactory.getLogger(T::class.java)
    }
}