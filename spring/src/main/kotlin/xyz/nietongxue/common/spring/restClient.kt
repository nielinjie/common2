package xyz.nietongxue.common.spring

import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient
import java.util.Base64

private fun RestClient.Builder.basicAuthentication(username: String, password: String): RestClient.Builder {
    val authHeader = "Basic " + Base64.getEncoder().encodeToString(("$username:$password").toByteArray())
    return this.defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)

}