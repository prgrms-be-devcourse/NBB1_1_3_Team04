package com.grepp.nbe1_3_team04.team.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.team.api.request.TeamMemberRequest
import com.grepp.nbe1_3_team04.team.service.TeamMemberService
import com.grepp.nbe1_3_team04.team.service.response.TeamResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails

@RestController
@RequestMapping("/api/v1/team-member")
class TeamMemberApi(
    private val teamMemberService: TeamMemberService
) {

    /**
     * 팀 멤버 추가
     */
    @PostMapping("/{teamId}/members")
    fun addTeamMembers(
        @PathVariable teamId: Long,
        @RequestBody request: TeamMemberRequest
    ): ApiResponse<List<TeamResponse>> {
        return ApiResponse.ok(teamMemberService.addTeamMembers(teamId, request.toServiceRequest()))
    }

    /**
     * 팀 멤버 전체 조회
     */
    @GetMapping("/{teamId}/members")
    fun getTeamMembers(@PathVariable teamId: Long): ApiResponse<List<TeamResponse>> {
        return ApiResponse.ok(teamMemberService.getTeamMembers(teamId))
    }

    /**
     * 팀 멤버 수정 - role
     */
    /**
     * 팀 탈퇴_팀장
     */
    @DeleteMapping("/{teamId}/members/{teamMemberId}")
    fun deleteTeamMemberByCreator(
        @PathVariable teamId: Long,
        @PathVariable teamMemberId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<Long> {
        teamMemberService.deleteTeamMemberByCreator(teamId, teamMemberId, principalDetails.member)
        return ApiResponse.ok(teamMemberId)
    }

    /**
     * 팀 탈퇴_본인
     */
    @DeleteMapping("/{teamId}/members")
    fun deleteTeamMember(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<Long> {
        val teamMemberId: Long = teamMemberService.deleteTeamMember(teamId, principalDetails.member)
        return ApiResponse.ok(teamMemberId)
    }
}
