package com.grepp.nbe1_3_team04.member.service

import com.grepp.nbe1_3_team04.config.SecurityConfig
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.jwt.JwtTokenFilter
import com.grepp.nbe1_3_team04.member.jwt.JwtTokenUtil
import com.grepp.nbe1_3_team04.member.jwt.response.TokenResponse
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import com.grepp.nbe1_3_team04.member.service.request.JoinServiceRequest
import com.grepp.nbe1_3_team04.member.service.request.LoginServiceRequest
import com.grepp.nbe1_3_team04.member.service.request.UpdatePasswordServiceRequest
import com.grepp.nbe1_3_team04.member.service.request.UpdateServiceRequest
import com.grepp.nbe1_3_team04.member.service.response.MemberResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class MemberServiceImpl(private val memberRepository: MemberRepository,
                        private val jwtSecurityConfig: SecurityConfig,
                        private val jwtTokenUtil: JwtTokenUtil,
                        private val redisTemplate: RedisTemplate<String, Any>,
                        private val passwordEncoder: BCryptPasswordEncoder) : MemberService {


    @Transactional
    override fun join(serviceRequest: JoinServiceRequest): MemberResponse {
        checkDuplicateEmail(serviceRequest.email)
        val member: Member = serviceRequest.toEntity()

        member.password?.let { member.encodePassword(jwtSecurityConfig.passwordEncoder()) }

        memberRepository.save(member)
        return MemberResponse.from(member)
    }

    @Transactional
    override fun login(serviceRequest: LoginServiceRequest): TokenResponse {
        val member: Member = getMemberByEmail(serviceRequest.email)
        checkPasswordMatch(serviceRequest.password, member.password)

        val tokenResponse: TokenResponse = jwtTokenUtil.createToken(member.email)

        // Redis에 RefreshToken 저장
        setRedis(member.email, tokenResponse.refreshToken, tokenResponse.refreshTokenExpirationTime, TimeUnit.MICROSECONDS)

        return tokenResponse
    }

    @Transactional
    override fun logout(request: HttpServletRequest): String {
        val accessToken: String? = jwtTokenUtil.resolveToken(request)
        val email: String = jwtTokenUtil.getEmailFromToken(accessToken)
        jwtTokenUtil.tokenValidation(accessToken)

        redisTemplate.opsForValue()[email]?.let {
            redisTemplate.delete(email)
        }

        val expiration: Long = jwtTokenUtil.getExpiration(accessToken!!) // tokenValidation 을 통해 accessToken 검증 완료됨
        setRedis(accessToken, "logout", expiration, TimeUnit.MICROSECONDS)

        return "Success Logout"
    }

    override fun reissue(request: HttpServletRequest, refreshToken: String?): TokenResponse {
        var token: String? = refreshToken
        if (token.isNullOrBlank()) {
            token = JwtTokenFilter.getRefreshTokenByRequest(request) // 헤더에 없을 경우 쿠키에서 꺼내 씀
        }
        jwtTokenUtil.tokenValidation(token)

        return convertToTokenResponseFrom(token!!)
    }

    @Transactional
    override fun update(member: Member, request: UpdateServiceRequest): MemberResponse {
        member.update(request.name, request.phoneNumber, request.gender)
        memberRepository.save(member)

        return MemberResponse.from(member)
    }

    @Transactional
    override fun updatePassword(member: Member, serviceRequest: UpdatePasswordServiceRequest): MemberResponse {
        checkPasswordMatch(serviceRequest.prePassword, member.password)
        member.changePassword(passwordEncoder.encode(serviceRequest.newPassword))
        memberRepository.save(member)

        return MemberResponse.from(member)
    }


    private fun getMemberByEmail(email: String): Member {
        return memberRepository.findByEmail(email) ?: throw IllegalArgumentException("존재하지 않는 사용자 입니다.")
    }

    private fun setRedis(key: String, value: String, expirationTime: Long, timeUnit: TimeUnit){
        redisTemplate.opsForValue().set(key, value, expirationTime, timeUnit)
    }

    private fun checkDuplicateEmail(email: String){
        require(!memberRepository.existByEmail(email)) { "이미 존재하는 이메일 입니다." }
    }

    private fun checkPasswordMatch(password: String?, passwordConfirm: String?){
        require(jwtSecurityConfig.passwordEncoder().matches(password, passwordConfirm)) { "패스워드가 일치하지 않습니다." }
    }

    private fun convertToTokenResponseFrom(token: String): TokenResponse{
        return TokenResponse.of(
            jwtTokenUtil.reCreateAccessToken(token),
            token,
            jwtTokenUtil.getExpiration(token)
        )
    }

}
