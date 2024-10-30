package com.grepp.nbe1_3_team04.team.repository

import com.grepp.nbe1_3_team04.team.domain.TeamMember

interface CustomTeamMemberRepository {
    fun findByTeamIdAndMemberId(teamId: Long, memberId: Long): TeamMember?
}
