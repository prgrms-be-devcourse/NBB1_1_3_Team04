package com.grepp.nbe1_3_team04.global.exception.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.nbe1_3_team04.global.api.ApiResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class ExceptionHandlerFilter : OncePerRequestFilter() {

    private val log: Logger = LoggerFactory.getLogger(ExceptionHandlerFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            when (e) {
                is JwtException -> {
                    log.error(e.message)
                    setErrorResponse(HttpStatus.UNAUTHORIZED, response, e)
                }

                else -> {
                    log.error(e.message)
                    setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e)
                }
            }
        }
    }

    private fun setErrorResponse(status: HttpStatus, response: HttpServletResponse, ex: Throwable) {
        val objectMapper = ObjectMapper()
        response.status = status.value()
        response.contentType = "application/json"
        val apiResponse = ApiResponse.of<Any?>(
            HttpStatus.UNAUTHORIZED,
            ex.message ?: "Unauthorized",
            null
        )
        try {
            val jsonResponse = objectMapper.writeValueAsString(apiResponse)
            response.characterEncoding = "UTF-8"
            response.writer.write(jsonResponse)
        } catch (e: IOException) {
            log.error("Error writing response", e)
        }
    }
}