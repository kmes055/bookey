package com.bookeyproject.bookey.common.controller

import com.bookeyproject.bookey.common.service.MonitorService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("monitor")
class MonitorController(val monitorService: MonitorService) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/l7check")
    fun check(response: HttpServletResponse): Mono<String> {
        log.info("status is: {}", monitorService.healthCheck())
        if (monitorService.healthCheck()) {
            return Mono.just("ok")
        }
        response.status = 503
        return Mono.empty()
    }

    @PostMapping("/enable")
    fun enable(): Mono<Void> {
        monitorService.enable()
        return Mono.empty()
    }

    @PostMapping("/disable")
    fun disable(): Mono<Void> {
        monitorService.disable()
        return Mono.empty()
    }
}