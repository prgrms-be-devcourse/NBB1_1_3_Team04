package com.grepp.nbe1_3_team04.member.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.*

@Component
class JwtTokenFilter(private val jwtTokenUtil: JwtTokenUtil, private val redisTemplate: RedisTemplate<String, *>) : OncePerRequestFilter() {

    private final val excludedUrls = listOf(
        "/api/v1/members/join",
        "/api/v1/members/login",
        "/api/v1/court/",
        "/api/v1/stadium/",
        "/api/v1/merchant/",
    )


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (isExcludedUrl(request.requestURI)) {
            filterChain.doFilter(request, response)
        }

        val accessToken = jwtTokenUtil.getHeaderToken(request, JwtTokenUtil.ACCESS_TOKEN)
        val refreshToken = getRefreshTokenByRequest(request) ?: jwtTokenUtil.getHeaderToken(request, JwtTokenUtil.REFRESH_TOKEN)

        processSecurity(accessToken, refreshToken)
        filterChain.doFilter(request, response)
    }

    @Throws(ServletException::class)
    private fun processSecurity(accessToken: String?, refreshToken: String?) {
        accessToken?.let{
            jwtTokenUtil.tokenValidation(it)
            val email = jwtTokenUtil.getEmailFromToken(it)
            val isLogout = redisTemplate.opsForValue()[it] as String?

            if(ObjectUtils.isEmpty(isLogout)){
                setAuthentication(email)
            }
        }

        if (accessToken == null && refreshToken != null) {
            if(!jwtTokenUtil.refreshTokenValidation(refreshToken)){
                return
            }
            setAuthentication(jwtTokenUtil.getEmailFromToken(refreshToken))
        }
    }

    fun setAuthentication(email: String?) {
        val authentication = jwtTokenUtil.createAuthentication(email)
        SecurityContextHolder.getContext().authentication = authentication
    }


    companion object {
        fun getRefreshTokenByRequest(request: HttpServletRequest): String? {
            val cookies: Array<Cookie>? = request.cookies

            if (!cookies.isNullOrEmpty()) {
                return Arrays.stream(cookies)
                    .filter { c: Cookie -> c.name == JwtTokenUtil.COOKIE_REFRESH_TOKEN }.findFirst().map { obj: Cookie -> obj.value }
                    .orElse(null)
            }

            return null
        }
    }

    private fun isExcludedUrl(requestURI: String): Boolean {
        return excludedUrls.any { requestURI.startsWith(it) }
    }

}
