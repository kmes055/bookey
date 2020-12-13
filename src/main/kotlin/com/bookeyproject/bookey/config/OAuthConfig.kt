package com.bookeyproject.bookey.config

import com.bookeyproject.bookey.constant.OAuthProvider
import com.bookeyproject.bookey.domain.BookeyUser
import com.bookeyproject.bookey.handler.UserHandler
import com.bookeyproject.bookey.repository.UserRepository
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.*
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.reactive.server.awaitSession
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class OAuthConfig(
    private val userHandler: UserHandler
) {
    private val log = KotlinLogging.logger { }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http.authorizeExchange()
                .pathMatchers("/admin/**").hasRole("ADMIN")
            .and()
            .authorizeExchange()
                .pathMatchers("/main/**").authenticated()
            .and()
            .authorizeExchange()
                .anyExchange().permitAll()
            .and()
            .oauth2Login()
            .and()
            .build()

}