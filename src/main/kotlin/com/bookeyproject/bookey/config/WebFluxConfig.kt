package com.bookeyproject.bookey.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebFluxConfig: WebFluxConfigurer {
    private val staticFiles = listOf("/css/*", "/js/*", "/favicon*", "/index.html", "/static/media/*")

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        super.configureArgumentResolvers(configurer)
    }


//    override fun addInterceptors(registry: InterceptorRegistry) {
//        registry.addInterceptor(MonitorFilter())
//                .addPathPatterns("/monitor/**")
//                .excludePathPatterns("/monitor/l7check")
//
//        registry.addInterceptor(LoginFilter(CookieService()))
//                .addPathPatterns("/**")
//                .excludePathPatterns("/", "/landing", "/login", "/register/*", "/oauth/*")
//                .excludePathPatterns(staticFiles)
//
//        registry.addInterceptor(NotLoginFilter(CookieService()))
//                .addPathPatterns("/login", "/register/*", "/oauth/*")
//
//        super.addInterceptors(registry)
//    }
}