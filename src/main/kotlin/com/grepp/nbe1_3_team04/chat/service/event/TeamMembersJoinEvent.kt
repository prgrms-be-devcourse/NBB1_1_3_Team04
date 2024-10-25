package com.grepp.nbe1_3_team04.chat.service.event

import com.grepp.nbe1_3_team04.team.domain.TeamMember


data class TeamMembersJoinEvent(
    val members: List<TeamMember>,
    val teamId: Long
)
