package com.grepp.nbe1_3_team04.member.oauth2

import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.domain.MemberRole
import com.grepp.nbe1_3_team04.member.jwt.JwtTokenUtil
import com.grepp.nbe1_3_team04.member.jwt.response.TokenResponse
import com.grepp.nbe1_3_team04.member.oauth2.response.MemberOAuthResponse
import com.grepp.nbe1_3_team04.member.service.CookieService
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.concurrent.TimeUnit

@Component
class CustomOAuth2LoginSuccessHandler(
    private val jwtTokenUtil: JwtTokenUtil,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val cookieService: CookieService
) : SimpleUrlAuthenticationSuccessHandler() {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.

        val oAuth2User = authentication.principal as CustomOAuth2UserDetails
        // 사용자를 가져온다.
        val member: Member? = oAuth2User.member

        val objectMapper = ObjectMapper()
        response.setContentType("application/json")
        response.setCharacterEncoding("UTF-8")

        member?.let {
            // 최초 로그인인 경우
            if (member.memberRole === MemberRole.GUEST) {
                val apiResponse: ApiResponse<MemberOAuthResponse> = ApiResponse.of(
                    HttpStatus.OK,
                    MemberOAuthResponse.from(member)
                )
                val jsonResponse = objectMapper.writeValueAsString(apiResponse)
                response.getWriter().write(jsonResponse)
                return
            }
            // 회원 가입 기록이 있으면
            val tokenResponse: TokenResponse = jwtTokenUtil.createToken(member.email)

            val apiResponse: ApiResponse<TokenResponse> = ApiResponse.of(
                HttpStatus.OK,
                tokenResponse
            )

            // Redis에 RefreshToken 저장
            redisTemplate.opsForValue().set(
                member.email,
                tokenResponse.refreshToken,
                tokenResponse.refreshTokenExpirationTime,
                TimeUnit.MICROSECONDS
            )
            cookieService.setHeader(response, tokenResponse.refreshToken) // 쿠키에 refreshToken 저장
            val jsonResponse = objectMapper.writeValueAsString(apiResponse)
            response.getWriter().write(jsonResponse)

        }
    }
}
