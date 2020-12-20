package com.bookeyproject.bookey.filter

import com.bookeyproject.bookey.constant.OAuthProvider
import com.bookeyproject.bookey.domain.BookeyUser
import com.bookeyproject.bookey.repository.UserRepository
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import mu.KotlinLogging
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException
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
    private val pathsForLoginUser = arrayOf("/main", "/user/nickname", "/admin")
    private val pathsForNotLoginUser = arrayOf("/", "/login", "/logon")

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return mono {
            getSecurityInfo(exchange)
                ?.let { handleLoginUser(exchange, it.authentication as OAuth2AuthenticationToken) }
                ?: handleNotLoginUser(exchange)

            chain.filter(exchange).awaitFirstOrNull()
        }
    }

    private suspend fun getSecurityInfo(exchange: ServerWebExchange): SecurityContext? =
        exchange.awaitSession().getAttribute<SecurityContextImpl>("SPRING_SECURITY_CONTEXT")

    private suspend fun handleLoginUser(exchange: ServerWebExchange, context: OAuth2AuthenticationToken) =
        exchange.request.uri
            .takeUnless { pathsForNotLoginUser.contains(it.toString()) }
            ?.let { configureAuthInfo(context) }
            ?.also { exchange.attributes["userId"] = it.userId }
            ?: redirectTo(exchange.response, "/bookmark")

    private suspend fun configureAuthInfo(context: OAuth2AuthenticationToken): BookeyUser =
        context.authorizedClientRegistrationId
            .let { OAuthProvider.of(it) }
            ?.let { userRepository.findBy(it, context.name)
                ?: register(it, context.name)
            }?: throw InvalidDataAccessApiUsageException("Invalid provider type")

    private suspend fun register(provider: OAuthProvider, userId: String): BookeyUser =
        when (provider) {
            OAuthProvider.GOOGLE -> BookeyUser(googleId = userId)
            OAuthProvider.NAVER -> BookeyUser(naverId = userId)
            OAuthProvider.KAKAO -> BookeyUser(kakaoId = userId)
            else -> throw InvalidDataAccessApiUsageException("Invalid provider type")
        }.also { userRepository.save(it) }

    private suspend fun redirectTo(response: ServerHttpResponse, path: String) =
        response.apply {
            statusCode = HttpStatus.FOUND
            headers["location"] = path
        }

    private suspend fun handleNotLoginUser(exchange: ServerWebExchange) =
        exchange.request.uri
            .takeUnless { pathsForLoginUser.contains(it.toString()) }
            ?: redirectTo(exchange.response, "/login")

}