package com.bookeyproject.bookey.client

import com.bookeyproject.bookey.constant.OAuthProperties
import com.bookeyproject.bookey.model.GoogleOAuthToken
import com.bookeyproject.bookey.model.GoogleUserInfo
import io.netty.handler.codec.http.HttpHeaderNames
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.net.URI

@Component
class GoogleOAuthClient(
        @Value("\${oauth.google.client-id")
        private val clientId: String,
        @Value("\${oauth.google.client-secret")
        private val clientSecret: String,
        @Value("\${oauth.google.redirect-uri}")
        private val redirectUri: String

) : OAuthClient {
    final override val baseUrl: String = "https://accounts.google.com/o/oauth2/v2"
    private final val responseType: String = "code"
    private final val grantType: String = "authorization_code"
    private final val scope: String = "openid"
    private final val client: WebClient

    init {
        client = WebClient.builder().baseUrl(baseUrl).build()
    }

    override fun getRedirectUrl(): String {
        val params = HashMap<String, String>()
                .setRedirectParam()

        return UriComponentsBuilder.fromUri(URI(baseUrl))
                .uriVariables(params as Map<String, Any>)
                .build()
                .toUriString()
    }

    override fun getOAuthToken(code: String, state: String): String {
        val params = HashMap<String, String>().setAuthRequestParam(code, state)
        return client.post()
                .uri(URI.create("/authorize"))
                .body(BodyInserters.fromProducer(Mono.just(params), String::class.java))
                .retrieve()
                .bodyToMono(GoogleOAuthToken::class.java)
                .blockOptional()
                .map { it.accessToken }
                .orElse(StringUtils.EMPTY)
    }

    override fun getUserInfo(token: String): String {
        return client.get()
                .uri("/userinfo")
                .header(HttpHeaderNames.AUTHORIZATION.toString(), "Bearer $token")
                .retrieve()
                .bodyToMono(GoogleUserInfo::class.java)
                .blockOptional()
                .map { it.id }
                .orElse(StringUtils.EMPTY)
    }

    private fun HashMap<String, String>.setRedirectParam(): HashMap<String, String> {
        this.let {
            it[OAuthProperties.CLIENT_ID.value] = clientId
            it[OAuthProperties.REDIRECT_URI.value] = redirectUri
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
            it[OAuthProperties.CODE.value] = code
            it[OAuthProperties.GRANT_TYPE.value] = grantType
            it[OAuthProperties.REDIRECT_URI.value] = redirectUri
            it[OAuthProperties.STATE.value] = state
        }

        return this
    }

    private val state = { RandomStringUtils.randomAlphanumeric(5) }
}