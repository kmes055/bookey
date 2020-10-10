package com.bookeyproject.bookey.interceptor

import org.springframework.web.servlet.HandlerInterceptor
import java.net.InetAddress
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MonitorInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val address: InetAddress = InetAddress.getByName(request.remoteAddr)
        return address.isAnyLocalAddress || address.isLinkLocalAddress
    }
}