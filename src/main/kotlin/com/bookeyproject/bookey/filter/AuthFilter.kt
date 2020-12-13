package com.bookeyproject.bookey.filter

import com.bookeyproject.bookey.constant.OAuthProvider
import com.bookeyproject.bookey.repository.UserRepository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.reactive.server.awaitSession
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class AuthFilter(
    private val userRepository: UserRepository
) : WebFilter {
    private val log = KotlinLogging.logger {}

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return mono {
            exchange.awaitSession().getAttribute<SecurityContextImpl>("SPRING_SECURITY_CONTEXT")
                ?.let { it.authentication as OAuth2AuthenticationToken }
                ?.let { auth ->
                    auth.authorizedClientRegistrationId
                        ?.let { OAuthProvider.of(it) }
                        ?.let { userRepository.findBy(it, auth.name) }
                }?.also { exchange.attributes["userId"] = it.userId }

            chain.filter(exchange).awaitFirstOrNull()
        }
    }
}