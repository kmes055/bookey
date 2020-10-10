package com.bookeyproject.bookey.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MonitorService {
    private val log = LoggerFactory.getLogger(javaClass)
    private var status: Boolean = true

    fun healthCheck(): Boolean {
        return status
    }

    fun enable() {
        this.status = true;
        log.info("Health check status set to {}", status);
    }

    fun disable() {
        this.status = false;
        log.info("Health check status set to {}", status);
    }
}