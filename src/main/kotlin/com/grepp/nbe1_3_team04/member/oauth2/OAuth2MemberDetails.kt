package com.grepp.nbe1_3_team04.member.oauth2

import com.grepp.nbe1_3_team04.member.domain.LoginProvider

interface OAuth2MemberDetails {
    val provider: LoginProvider

    val snsId: String

    val email: String

    val name: String
}
