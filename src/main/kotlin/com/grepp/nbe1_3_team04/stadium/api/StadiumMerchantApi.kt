package com.grepp.nbe1_3_team04.stadium.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.stadium.api.request.StadiumRegisterRequest
import com.grepp.nbe1_3_team04.stadium.api.request.StadiumUpdateRequest
import com.grepp.nbe1_3_team04.stadium.service.StadiumService
import com.grepp.nbe1_3_team04.stadium.service.response.StadiumDetailResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails

@RestController
@RequestMapping("/api/v1/merchant/stadium")
class StadiumMerchantApi(
    private val stadiumService: StadiumService
) {

    companion object {
        private val log = LoggerFactory.getLogger(StadiumMerchantApi::class.java)
    }

    @PostMapping("/register")
    fun registerStadium(
        @Validated @RequestBody request: StadiumRegisterRequest,
        @AuthenticationPrincipal currentUser: PrincipalDetails
    ): ApiResponse<StadiumDetailResponse> {
        val stadiumDetail = stadiumService.registerStadium(request.toServiceRequest(), currentUser.member)
        return ApiResponse.created(stadiumDetail)
    }

    @PutMapping("/{stadiumId}")
    fun updateStadium(
        @PathVariable stadiumId: Long,
        @Validated @RequestBody request: StadiumUpdateRequest,
        @AuthenticationPrincipal currentUser: PrincipalDetails
    ): ApiResponse<StadiumDetailResponse> {
        val stadiumDetail = stadiumService.updateStadium(request.toServiceRequest(), currentUser.member, stadiumId)
        return ApiResponse.ok(stadiumDetail)
    }

    @DeleteMapping("/{stadiumId}")
    fun deleteStadium(
        @PathVariable stadiumId: Long,
        @AuthenticationPrincipal currentUser: PrincipalDetails
    ): ApiResponse<Unit> {
        stadiumService.deleteStadium(currentUser.member, stadiumId)
        return ApiResponse.ok(Unit)
    }
}