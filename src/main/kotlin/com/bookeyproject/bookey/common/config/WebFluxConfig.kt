package com.bookeyproject.bookey.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebFluxConfig: WebFluxConfigurer {
    private val staticFiles = listOf("/css/*", "/js/*", "/favicon*", "/index.html", "/static/media/*")

    override fun addCorsMappings(corsRegistry: CorsRegistry): Unit {
        corsRegistry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .maxAge(3600);
    }
}