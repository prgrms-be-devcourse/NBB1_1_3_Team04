package team4.footwithme.member.jwt

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.domain.MemberRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class PrincipalDetails(val member: Member, private val authorities: Collection<GrantedAuthority?>) : UserDetails {
    val memberRole: MemberRole
        get() = member.memberRole

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return authorities
    }

    override fun getPassword(): String {
        return member.password!!
    }

    override fun getUsername(): String {
        return member.email
    }
}
