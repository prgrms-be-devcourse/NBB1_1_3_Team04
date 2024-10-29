package com.grepp.nbe1_3_team04.reservation.service.response

import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.domain.MemberRole


data class ParticipantMemberInfo(
    val memberId: Long,
    val email: String,
    val name: String,
    val memberRole: MemberRole
) {
    constructor(member: Member) : this(
        requireNotNull(member.memberId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
        member.email,
        member.name,
        member.memberRole
    )
}
