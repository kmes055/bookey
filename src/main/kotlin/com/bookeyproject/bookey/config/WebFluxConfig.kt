package com.bookeyproject.bookey.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebFluxConfig: WebFluxConfigurer {
    private val staticFiles = listOf("/css/*", "/js/*", "/favicon*", "/index.html", "/static/media/*")

}