package com.grepp.nbe1_3_team04.member.oauth2.response

import com.grepp.nbe1_3_team04.member.domain.LoginProvider
import com.grepp.nbe1_3_team04.member.domain.Member

class MemberOAuthResponse(
    val provider: LoginProvider,
    val snsId: String?,
    val email: String
) {

    companion object {
        fun from(member: Member): MemberOAuthResponse {
            return MemberOAuthResponse(
                member.loginType.loginProvider,
                member.loginType.snsId,
                member.email
            )
        }
    }
}
