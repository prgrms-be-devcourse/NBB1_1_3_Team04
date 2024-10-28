package com.grepp.nbe1_3_team04.member.oauth2

import com.grepp.nbe1_3_team04.member.domain.*
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(private val memberRepository: MemberRepository) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User: OAuth2User = super.loadUser(userRequest)

        val provider: String = userRequest.getClientRegistration().getRegistrationId()

        var oAuth2MemberDetails: OAuth2MemberDetails? = null
        var member : Member? = null

        // 언젠간 진행할 다른 소셜 서비스 로그인을 위해 구분 => 구글
        if (provider == "google") {
            log.info("구글 로그인")
            oAuth2MemberDetails = OAuth2GoogleMemberDetails(oAuth2User.attributes)
        }

        oAuth2MemberDetails?.let {
            val snsId: String = oAuth2MemberDetails.snsId
            val email: String = oAuth2MemberDetails.email
            val name: String = oAuth2MemberDetails.name
            val loginProvider: LoginProvider = oAuth2MemberDetails.provider

            member = memberRepository.findByEmail(email) ?: Member.create(email, null, name, "", loginProvider, snsId, Gender.TEMP, MemberRole.GUEST, TermsAgreed.DISAGREE)
        }


        return CustomOAuth2UserDetails(member, oAuth2User.getAttributes())
    }
}