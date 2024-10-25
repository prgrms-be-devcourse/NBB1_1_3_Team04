package com.grepp.nbe1_3_team04.member.jwt

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import team4.footwithme.member.jwt.PrincipalDetails

@Service
class PrincipalDetailsService(private val memberRepository: MemberRepository) : UserDetailsService {


    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val member: Member = memberRepository.findByEmail(email)
            ?: throw IllegalArgumentException("존재하지 않는 이메일 입니다.")

        val grantedAuthority: GrantedAuthority = SimpleGrantedAuthority(member.memberRole.text)

        val userDetails = PrincipalDetails(member, setOf(grantedAuthority))

        return userDetails
    }
}
