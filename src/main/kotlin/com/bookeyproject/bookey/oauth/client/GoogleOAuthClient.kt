package com.bookeyproject.bookey.oauth.client

import com.bookeyproject.bookey.oauth.constant.OAuthProperties
import com.bookeyproject.bookey.oauth.domain.GoogleOAuthToken
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import mu.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Component
class GoogleOAuthClient(
    @Value("\${oauth.google.client-id}")
    private val clientId: String,
    @Value("\${oauth.google.client-secret}")
    private val clientSecret: String,
    @Value("\${oauth.google.redirect-uri}")
    private val redirectUri: String

) : OAuthClient {
    private val log = KotlinLogging.logger {  }

    final override val baseUrl: String = "https://oauth2.googleapis.com"
    private final val authUrl: String = "https://accounts.google.com/o/oauth2/v2/auth"
    private final val responseType: String = "code"
    private final val grantType: String = "authorization_code"
    private final val scope: String = "openid"

    private val client: WebClient = WebClient.builder().exchangeStrategies(
        ExchangeStrategies.builder().codecs { configurer: ClientCodecConfigurer ->
            configurer.defaultCodecs().jackson2JsonDecoder(
                Jackson2JsonDecoder(
                    ObjectMapper().apply {
                        propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
                        registerModule(KotlinModule())
                    }
                )
            )
        }.build()
    ).build()

    override fun getRedirectUrl(): String {
        val params = LinkedMultiValueMap<String, String>().setRedirectParam()

        return UriComponentsBuilder.fromUriString(authUrl)
            .queryParams(params)
            .build()
            .toUriString()
    }

    override fun getOAuthToken(code: String, state: String): String {
        val params = HashMap<String, String>().setAuthRequestParam(code, state)
        return client.post()
            .uri(URI.create("$baseUrl/token"))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(params.entries.map { "${it.key}=${it.value}" }.reduce { acc, s -> "$acc&$s" })
            .retrieve()
            .bodyToMono(GoogleOAuthToken::class.java)
            .blockOptional()
            .map { it.idToken }
            .orElseGet {
                log.info("Token not found")
                StringUtils.EMPTY
            }
    }

    override fun getUserInfo(token: String): String {
        val jwt: JWT = JWTParser.parse(token)
        val sub = jwt.jwtClaimsSet.getClaim("sub") as String
        return sub
    }

    private fun LinkedMultiValueMap<String, String>.setRedirectParam(): LinkedMultiValueMap<String, String> {
        this.let {
            it[OAuthProperties.CLIENT_ID.value] = clientId
            it[OAuthProperties.REDIRECT_URI.value] = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
            it[OAuthProperties.RESPONSE_TYPE.value] = responseType
            it[OAuthProperties.STATE.value] = state()
            it[OAuthProperties.SCOPE.value] = scope
        }

        return this
    }

    private fun HashMap<String, String>.setAuthRequestParam(code: String, state: String): HashMap<String, String> {
        this.let {
            it[OAuthProperties.CLIENT_ID.value] = clientId
            it[OAuthProperties.CLIENT_SECRET.value] = clientSecret
            it[OAuthProperties.CODE.value] = URLEncoder.encode(code, StandardCharsets.UTF_8)
            it[OAuthProperties.GRANT_TYPE.value] = grantType
            it[OAuthProperties.REDIRECT_URI.value] = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
            it[OAuthProperties.STATE.value] = state
        }

        return this
    }

    private val state = { RandomStringUtils.randomAlphanumeric(5) }
}
