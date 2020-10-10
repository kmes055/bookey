package com.bookeyproject.bookey.service

import org.springframework.stereotype.Service

@Service
class MonitorService {
    private var status: Boolean = true

    fun healthCheck(): Boolean {
        return status
    }

    fun enable() {
        this.status = true;
    }

    fun disable() {
        this.status = false;
    }
}