package com.grepp.nbe1_3_team04.member.service.request

import com.grepp.nbe1_3_team04.member.domain.*

data class JoinServiceRequest(
    val email: String,
    val password: String?,
    val name: String,
    val phoneNumber: String,
    val loginProvider: LoginProvider,
    val snsId: String?,
    val gender: Gender,
    val memberRole: MemberRole,
    val termsAgree: TermsAgreed
) {
    fun toEntity(): Member {
        return Member.create(email, password, name, phoneNumber, loginProvider, snsId, gender, memberRole, termsAgree)
    }
}
