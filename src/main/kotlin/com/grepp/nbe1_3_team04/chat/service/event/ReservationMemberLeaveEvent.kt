package com.grepp.nbe1_3_team04.chat.service.event

import com.grepp.nbe1_3_team04.member.domain.Member


data class ReservationMemberLeaveEvent(
    val member: Member,
    val reservationId: Long
)
