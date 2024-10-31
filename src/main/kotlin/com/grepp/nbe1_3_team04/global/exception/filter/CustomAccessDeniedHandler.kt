package com.grepp.nbe1_3_team04.global.exception.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.nbe1_3_team04.global.api.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {
        val objectMapper = ObjectMapper()
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"
        val apiResponse: ApiResponse<Any?> = ApiResponse.of(
            HttpStatus.FORBIDDEN,
            "권한이 없습니다.",
            null
        )

        val jsonResponse = objectMapper.writeValueAsString(apiResponse)
        response.characterEncoding = "UTF-8"
        response.writer.write(jsonResponse)
    }
}