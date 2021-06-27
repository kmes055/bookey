package com.bookeyproject.bookey.auth.client

import com.bookeyproject.bookey.web.makeQueryString
import org.springframework.stereotype.Component

@Component
class NaverOAuthClient : OAuthClient {
    override val baseUrl = "https://nid.naver.com/authorize"

    override fun getRedirectUrl(): String {
        val params = HashMap<String, String>()
        params["redirect_uri"] = ""
        params["client_id"] = ""
        params[""]

        return "%s%s".format(baseUrl, makeQueryString(params))
    }

    override fun getOAuthToken(code: String, state: String): String {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(token: String): String {
        TODO("Not yet implemented")
    }
}