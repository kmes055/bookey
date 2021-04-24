package com.bookeyproject.bookey.common.util

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitSession

class HttpUtil {
    companion object {
        fun makeQueryString(params: Map<String, String>): String {
            return "?" +
                    params.map { entry -> entry.key + "=" + entry.value }
                        .joinToString("&")
        }

        suspend fun extractProviderName(request: ServerRequest): String? =
            request.awaitSession().getAttribute<SecurityContext>("SPRING_SECURITY_CONTEXT")
                ?.let { it.authentication as OAuth2AuthenticationToken }
                ?.authorizedClientRegistrationId
    }
}