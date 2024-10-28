package com.grepp.nbe1_3_team04.member.service

import com.grepp.nbe1_3_team04.member.jwt.JwtTokenUtil
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class CookieService {
    fun setHeader(response: HttpServletResponse, refreshToken: String?) {
        refreshToken?.let{
            response.addHeader(JwtTokenUtil.REFRESH_TOKEN, refreshToken)
            response.addHeader("Set-Cookie", createRefreshToken(refreshToken).toString())
        }
    }

    companion object {
        fun createRefreshToken(refreshToken: String): ResponseCookie {
            return ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .maxAge((14 * 24 * 60 * 60 * 1000).toLong())
                .httpOnly(true)
                .build()
        }
    }
}
