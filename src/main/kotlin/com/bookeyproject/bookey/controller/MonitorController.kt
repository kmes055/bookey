package com.bookeyproject.bookey.controller

import com.bookeyproject.bookey.service.MonitorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("monitor")
class MonitorController(val monitorService: MonitorService) {

    @GetMapping("/l7check")
    fun check(response: HttpServletResponse): String {
        if (monitorService.healthCheck()) {
            return "ok"
        }
        response.status = 404
        return ""
    }

    @PostMapping("/enable")
    fun enable() {
        monitorService.enable()
    }

    @PostMapping("/disable")
    fun disable() {
        monitorService.disable()
    }
}