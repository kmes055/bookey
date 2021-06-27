package com.bookeyproject.bookey.web.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebFluxConfig : WebFluxConfigurer {
    private val staticFiles = listOf("/css/*", "/js/*", "/favicon*", "/index.html", "/static/media/*")

    override fun addCorsMappings(corsRegistry: CorsRegistry): Unit {
        corsRegistry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("*")
            .maxAge(3600);
    }

}