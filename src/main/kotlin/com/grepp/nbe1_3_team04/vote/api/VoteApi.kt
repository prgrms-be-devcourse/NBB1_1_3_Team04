package com.grepp.nbe1_3_team04.vote.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.vote.api.request.VoteCourtCreateRequest
import com.grepp.nbe1_3_team04.vote.api.request.VoteDateCreateRequest
import com.grepp.nbe1_3_team04.vote.api.request.VoteUpdateRequest
import com.grepp.nbe1_3_team04.vote.service.VoteService
import com.grepp.nbe1_3_team04.vote.service.response.VoteResponse
import com.grepp.nbe1_3_team04.vote.service.response.AllVoteResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails

@RestController
@RequestMapping("/api/v1/votes")
class VoteApi(private val voteService: VoteService) {

    @PostMapping("/stadiums/{teamId}")
    fun createLocateVote(
        @RequestBody request: @Valid VoteCourtCreateRequest,
        @PathVariable teamId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<VoteResponse> {
        return ApiResponse.created(
            voteService.createCourtVote(
                request.toServiceRequest(),
                teamId,
                principalDetails.member
            )
        )
    }

    @GetMapping("{voteId}")
    fun getVote(@PathVariable voteId: Long): ApiResponse<VoteResponse> {
        return ApiResponse.ok(voteService.getVote(voteId))
    }

    @PostMapping("/dates/{teamId}")
    fun createDateVote(
        @RequestBody request: @Valid VoteDateCreateRequest,
        @PathVariable teamId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<VoteResponse> {
        return ApiResponse.created(
            voteService.createDateVote(
                request.toServiceRequest(),
                teamId,
                principalDetails.member()
            )
        )
    }

    @DeleteMapping("{voteId}")
    fun deleteVote(
        @PathVariable voteId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<Long> {
        return ApiResponse.ok(voteService.deleteVote(voteId, principalDetails.member()))
    }

    @PutMapping("{voteId}")
    fun updateVote(
        @RequestBody request: @Valid VoteUpdateRequest,
        @PathVariable voteId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<VoteResponse> {
        return ApiResponse.ok(voteService.updateVote(request.toServiceRequest(), voteId, principalDetails.member))
    }

    @PostMapping("/close/{voteId}")
    fun closeVote(
        @PathVariable voteId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<VoteResponse> {
        return ApiResponse.ok(voteService.closeVote(voteId, principalDetails.member))
    }

    @GetMapping("/all/{teamId}")
    fun getAllVotes(@PathVariable teamId: Long): ApiResponse<List<AllVoteResponse>> {
        return ApiResponse.ok(voteService.getAllVotes(teamId))
    }
}
