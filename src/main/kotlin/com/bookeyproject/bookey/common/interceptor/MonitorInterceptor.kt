package com.bookeyproject.bookey.common.interceptor

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor
import java.net.InetAddress
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MonitorInterceptor : HandlerInterceptor {
    val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val address: InetAddress = InetAddress.getByName(request.remoteAddr)
        if (address.isAnyLocalAddress || address.isLoopbackAddress) {
            return true
        }

        log.info("Blocked bad request from: {}", request.remoteAddr)
        response.status = 503
        return false
    }
}