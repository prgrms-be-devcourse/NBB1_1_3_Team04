package com.grepp.nbe1_3_team04.team.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.team.service.request.TeamMemberServiceRequest
import com.grepp.nbe1_3_team04.team.service.response.TeamResponse


interface TeamMemberService {
    fun addTeamMembers(teamId: Long, request: TeamMemberServiceRequest): List<TeamResponse>

    fun getTeamMembers(teamId: Long): List<TeamResponse>

    fun deleteTeamMemberByCreator(teamId: Long, teamMemberId: Long, member: Member): Long

    fun deleteTeamMember(teamId: Long, member: Member): Long
}
