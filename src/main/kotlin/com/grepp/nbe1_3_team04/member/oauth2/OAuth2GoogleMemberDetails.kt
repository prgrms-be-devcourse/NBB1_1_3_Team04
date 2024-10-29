package com.grepp.nbe1_3_team04.member.oauth2

import com.grepp.nbe1_3_team04.member.domain.LoginProvider

class OAuth2GoogleMemberDetails(private val attributes : Map<String, Any>) : OAuth2MemberDetails {

    override val provider: LoginProvider
        get() = LoginProvider.GOOGLE

    override val snsId: String
        get() = attributes["sub"] as String

    override val email: String
        get() = attributes["email"] as String

    override val name: String
        get() = attributes["name"] as String
}
