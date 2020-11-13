package com.bookeyproject.bookey.config

import com.bookeyproject.bookey.interceptor.LoginInterceptor
import com.bookeyproject.bookey.interceptor.MonitorInterceptor
import com.bookeyproject.bookey.interceptor.NotLoginInterceptor
import com.bookeyproject.bookey.service.CookieService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class InterceptorConfig: WebMvcConfigurer {
    private val staticFiles = listOf("/**/*.html", "/**/*.jpg", "/**/*.css", "/**/*.js", "/**/*.png")


    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(MonitorInterceptor())
                .addPathPatterns("/monitor/**")
                .excludePathPatterns("/monitor/l7check")

        registry.addInterceptor(LoginInterceptor(CookieService()))
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/landing", "/login", "/register/*", "/oauth/*")
                .excludePathPatterns(staticFiles)

        registry.addInterceptor(NotLoginInterceptor(CookieService()))
                .addPathPatterns("/login", "/register/*", "/oauth/*")

        super.addInterceptors(registry)
    }
}