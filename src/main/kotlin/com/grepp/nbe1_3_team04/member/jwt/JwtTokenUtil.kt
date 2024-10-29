package com.grepp.nbe1_3_team04.member.jwt

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.domain.MemberRole
import com.grepp.nbe1_3_team04.member.jwt.response.TokenResponse
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.security.Key
import java.time.Duration
import java.util.*

@Component
class JwtTokenUtil(private val userDetailService: PrincipalDetailsService, private val memberRepository: MemberRepository, private val redisTemplate: RedisTemplate<String, *>) {

    @Value("\${jwt.secret}")
    private val secretKey: String? = null
    private var key: Key? = null

    @PostConstruct
    fun init() {
        val bytes = Base64.getDecoder().decode(secretKey)
        key = Keys.hmacShaKeyFor(bytes)
    }

    fun getHeaderToken(request: HttpServletRequest, type: String): String? {
        if (type == ACCESS_TOKEN) return resolveToken(request)

        return request.getHeader(REFRESH_TOKEN)
    }

    fun createToken(email: String): TokenResponse {
        val role = getRoleFromEmail(email)

        val accessToken = createAccessToken(email, role)
        val refreshToken = createRefreshToken(email, role)

        return TokenResponse(accessToken, refreshToken, REFRESH_TIME)
    }

    fun createAccessToken(email: String?, role: MemberRole): String {
        val date = Date()

        return Jwts.builder()
            .setSubject(email)
            .claim("role", role.name)
            .setExpiration(Date(date.time + ACCESS_TIME))
            .setIssuedAt(date)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun reCreateAccessToken(refreshToken: String?): String {
        val date = Date()
        val email = getEmailFromToken(refreshToken)
        val role = getRoleFromEmail(email)

        return Jwts.builder()
            .setSubject(email)
            .claim("role", role.name)
            .setExpiration(Date(date.time + ACCESS_TIME))
            .setIssuedAt(date)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun createRefreshToken(email: String?, role: MemberRole?): String {
        val date = Date()

        val refreshToken = Jwts.builder()
            .setSubject(email)
            .setExpiration(Date(date.time + REFRESH_TIME))
            .setIssuedAt(date)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return refreshToken
    }

    fun createAuthentication(email: String?): Authentication {
        val userDetails = userDetailService.loadUserByUsername(email!!)
        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getExpiration(accessToken: String?): Long {
        val expiration: Date =
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration()

        val now = Date().time
        return (expiration.time - now)
    }

    private fun getRoleFromEmail(email: String): MemberRole {
        val member: Member = memberRepository.findByEmail(email) ?: throw IllegalArgumentException("존재하지 않는 사용자 입니다.")
        return member.memberRole
    }

    fun getEmailFromToken(token: String?): String {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject()
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val token = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (StringUtils.hasText(token)) {
            if (token.startsWith(BEARER_PREFIX)) {
                return token.substring(7).trim { it <= ' ' }
            }
            return token
        }

        return null
    }

    fun tokenValidation(token: String?) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        } catch (e: SecurityException) {
            throw JwtException("유효하지 않은 JWT 토큰입니다.")
        } catch (e: MalformedJwtException) {
            throw JwtException("유효하지 않은 JWT 토큰입니다.")
        } catch (e: ExpiredJwtException) {
            throw JwtException("만료된 JWT 입니다.")
        } catch (e: UnsupportedJwtException) {
            throw JwtException("지원하지 않은 JWT 입니다.")
        } catch (e: IllegalArgumentException) {
            throw JwtException("JWT 값이 비어있습니다.")
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }

    fun refreshTokenValidation(refreshToken: String?) {
        tokenValidation(refreshToken)
        val email = getEmailFromToken(refreshToken)
        val redisRefresh = redisTemplate.opsForValue()[email]

        redisRefresh?.toString() ?: throw JwtException("유효하지 않은 Jwt 토큰입니다.")
    }

    companion object {
        const val ACCESS_TOKEN: String = "Authorization"
        const val REFRESH_TOKEN: String = "refresh_token"
        const val BEARER_PREFIX: String = "Bearer "
        val ACCESS_TIME: Long = Duration.ofMinutes(30).toMillis() // 만료시간 30분
        val REFRESH_TIME: Long = Duration.ofDays(14).toMillis() // 만료시간 2주
    }
}
