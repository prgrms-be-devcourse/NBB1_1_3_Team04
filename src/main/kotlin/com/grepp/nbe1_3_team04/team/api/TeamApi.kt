package com.grepp.nbe1_3_team04.team.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.team.api.request.TeamCreateRequest
import com.grepp.nbe1_3_team04.team.api.request.TeamUpdateRequest
import com.grepp.nbe1_3_team04.team.service.TeamService
import com.grepp.nbe1_3_team04.team.service.response.TeamDefaultResponse
import com.grepp.nbe1_3_team04.team.service.response.TeamInfoResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails


@RestController
@RequestMapping("/api/v1/team")
class TeamApi(
    private val teamService: TeamService
) {

    /**
     * 팀 생성
     */
    @PostMapping("/create")
    fun createTeam(
        @RequestBody request: TeamCreateRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<TeamDefaultResponse> {
        return ApiResponse.created(teamService.createTeam(request.toServiceRequest(), principalDetails.member))
    }

    /**
     * 팀 정보 조회
     */
    @GetMapping("/{teamId}/info")
    fun getTeamInfo(@PathVariable teamId: Long): ApiResponse<TeamInfoResponse> {
        val teamInfoResponse: TeamInfoResponse = teamService.getTeamInfo(teamId)
        return ApiResponse.ok(teamInfoResponse)
    }

    /**
     * 팀 정보 수정
     */
    @PutMapping("/{teamId}/info")
    fun updateTeamInfo(
        @PathVariable teamId: Long,
        @RequestBody request: TeamUpdateRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<TeamDefaultResponse> {
        val teamUpdateResponse: TeamDefaultResponse =
            teamService.updateTeamInfo(teamId, request.toServiceRequest(), principalDetails.member)
        return ApiResponse.ok(teamUpdateResponse)
    }

    /**
     * 팀 삭제
     */
    @DeleteMapping("/{teamId}")
    fun deleteTeam(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<Long> {
        //요청받은 id리턴
        val deletedId: Long = teamService.deleteTeam(teamId, principalDetails.member)
        return ApiResponse.ok(deletedId)
    }
}
