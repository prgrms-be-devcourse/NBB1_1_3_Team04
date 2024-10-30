package com.grepp.nbe1_3_team04.stadium.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.stadium.api.request.CourtDeleteRequest
import com.grepp.nbe1_3_team04.stadium.api.request.CourtRegisterRequest
import com.grepp.nbe1_3_team04.stadium.api.request.CourtUpdateRequest
import com.grepp.nbe1_3_team04.stadium.service.CourtService
import com.grepp.nbe1_3_team04.stadium.service.response.CourtDetailResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails

@RestController
@RequestMapping("/api/v1/merchant/court")
class CourtMerchantApi(
    private val courtService: CourtService
) {

    companion object {
        private val log = LoggerFactory.getLogger(CourtMerchantApi::class.java)
    }

    @PostMapping("/register")
    fun registerCourt(
        @Validated @RequestBody request: CourtRegisterRequest,
        @AuthenticationPrincipal currentUser: PrincipalDetails
    ): ApiResponse<CourtDetailResponse> {
        val courtDetail = courtService.registerCourt(request.toServiceRequest(), currentUser.member)
        return ApiResponse.created(courtDetail)
    }

    @PutMapping("/{courtId}")
    fun updateCourt(
        @PathVariable courtId: Long,
        @Validated @RequestBody request: CourtUpdateRequest,
        @AuthenticationPrincipal currentUser: PrincipalDetails
    ): ApiResponse<CourtDetailResponse> {
        val courtDetail = courtService.updateCourt(request.toServiceRequest(), currentUser.member, courtId)
        return ApiResponse.ok(courtDetail)
    }

    @DeleteMapping("/{courtId}")
    fun deleteCourt(
        @PathVariable courtId: Long,
        @Validated @RequestBody request: CourtDeleteRequest,
        @AuthenticationPrincipal currentUser: PrincipalDetails
    ): ApiResponse<Unit> {
        courtService.deleteCourt(request.toServiceRequest(), currentUser.member, courtId)
        return ApiResponse.ok(Unit)
    }
}