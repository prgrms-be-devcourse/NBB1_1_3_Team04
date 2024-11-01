package com.grepp.nbe1_3_team04.member.oauth2.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.nbe1_3_team04.global.api.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class CustomOAuth2LoginFailureHandler : AuthenticationFailureHandler {
    private val objectMapper = ObjectMapper()

    override fun onAuthenticationFailure(request: HttpServletRequest?, response: HttpServletResponse?, exception: AuthenticationException?) {
        response?.status = HttpServletResponse.SC_UNAUTHORIZED
        response?.contentType = "application/json"
        response?.characterEncoding = "UTF-8"

        val apiResponse = ApiResponse.of(HttpStatus.UNAUTHORIZED, "로그인 실패", null)
        val jsonResponse = objectMapper.writeValueAsString(apiResponse)

        response?.writer?.write(jsonResponse)
    }
}