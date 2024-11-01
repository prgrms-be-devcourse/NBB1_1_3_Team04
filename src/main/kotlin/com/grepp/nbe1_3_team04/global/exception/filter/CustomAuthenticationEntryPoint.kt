package com.grepp.nbe1_3_team04.global.exception.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.nbe1_3_team04.global.api.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException


@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        authException: org.springframework.security.core.AuthenticationException?
    ) {
        val objectMapper = ObjectMapper()
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        val apiResponse: ApiResponse<Any?> = ApiResponse.of(
            HttpStatus.UNAUTHORIZED,
            "요청 혹은 인증 정보에 오류가 있습니다.",
            null
        )

        val jsonResponse = objectMapper.writeValueAsString(apiResponse)
        response.characterEncoding = "UTF-8"
        response.writer.write(jsonResponse)
    }
}