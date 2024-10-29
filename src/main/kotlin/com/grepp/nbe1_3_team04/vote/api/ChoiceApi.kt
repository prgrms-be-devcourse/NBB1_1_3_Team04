package com.grepp.nbe1_3_team04.vote.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.vote.api.request.ChoiceCreateRequest
import com.grepp.nbe1_3_team04.vote.service.VoteService
import com.grepp.nbe1_3_team04.vote.service.response.VoteResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails

@RestController
@RequestMapping("/api/v1/choice")
class ChoiceApi(private val voteService: VoteService) {
    @PostMapping("/{voteId}")
    fun createChoice(
        @RequestBody request: @Valid ChoiceCreateRequest,
        @PathVariable voteId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<VoteResponse> {
        return ApiResponse.created(
            voteService.createChoice(
                request.toServiceRequest(),
                voteId,
                principalDetails.member
            )
        )
    }

    @DeleteMapping("/{voteId}")
    fun deleteChoice(
        @PathVariable voteId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<VoteResponse> {
        return ApiResponse.ok(voteService.deleteChoice(voteId, principalDetails.member))
    }
}
