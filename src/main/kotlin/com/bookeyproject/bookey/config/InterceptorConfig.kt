package com.bookeyproject.bookey.config

import com.bookeyproject.bookey.interceptor.LoginInterceptor
import com.bookeyproject.bookey.interceptor.MonitorInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class InterceptorConfig: WebMvcConfigurer {


    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(MonitorInterceptor())
                .addPathPatterns("/monitor/**")
                .excludePathPatterns("/monitor/l7check")

        registry.addInterceptor(LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/landing", "/login", "/register")
                .excludePathPatterns("*.jpg", "*.css", "*.js", ".png")
        super.addInterceptors(registry);
    }
}