package com.grepp.nbe1_3_team04.team.service.response

import com.grepp.nbe1_3_team04.team.domain.TeamMember
import com.grepp.nbe1_3_team04.team.domain.TeamMemberRole

class TeamResponse(
    val teamMemberId: Long,
    val teamId: Long,
    val name: String,
    val role: TeamMemberRole
) {
    constructor(teamMember: TeamMember) : this(
        requireNotNull(teamMember.teamMemberId),
        requireNotNull(teamMember.team.teamId),
        teamMember.team.name,
        teamMember.role
    )
}
