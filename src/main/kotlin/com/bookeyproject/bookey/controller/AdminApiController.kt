package com.bookeyproject.bookey.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/admin/api")
class AdminApiController {

    @PostMapping("/notify")
    fun updateResources() {
        TODO("Update callback ")
    }
}