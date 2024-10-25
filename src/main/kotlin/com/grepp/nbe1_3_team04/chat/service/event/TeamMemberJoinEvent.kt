package com.grepp.nbe1_3_team04.chat.service.event

import com.grepp.nbe1_3_team04.member.domain.Member


data class TeamMemberJoinEvent(
    val member: Member,
    val teamId: Long
)
