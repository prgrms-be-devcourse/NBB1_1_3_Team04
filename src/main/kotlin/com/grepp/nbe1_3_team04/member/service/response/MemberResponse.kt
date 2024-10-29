package com.grepp.nbe1_3_team04.member.service.response

import com.grepp.nbe1_3_team04.member.domain.Gender
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.domain.MemberRole
import com.grepp.nbe1_3_team04.member.domain.TermsAgreed

data class MemberResponse(
    val memberId: Long,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val gender: Gender,
    val memberRole: MemberRole,
    val termsAgreed: TermsAgreed
) {
    companion object {
        fun from(member: Member): MemberResponse {
            return MemberResponse(
                requireNotNull(member.memberId),
                member.email,
                member.name,
                member.phoneNumber,
                member.gender,
                member.memberRole,
                member.termsAgreed
            )
        }
    }
}
