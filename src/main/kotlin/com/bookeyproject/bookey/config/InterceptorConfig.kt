package com.bookeyproject.bookey.config

import com.bookeyproject.bookey.interceptor.MonitorInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class InterceptorConfig: WebMvcConfigurer {

    fun monitorInterceptor(): MonitorInterceptor {
        return MonitorInterceptor();
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(monitorInterceptor())
                .addPathPatterns("/monitor/**")
                .excludePathPatterns("/monitor/l7check");
        super.addInterceptors(registry);
    }
}