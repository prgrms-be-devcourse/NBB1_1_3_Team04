package com.grepp.nbe1_3_team04.reservation.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.reservation.api.request.MercenaryRequest
import com.grepp.nbe1_3_team04.reservation.service.MercenaryService
import com.grepp.nbe1_3_team04.reservation.service.response.MercenaryResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails


@RestController
@RequestMapping("/api/v1/mercenary")
class MercenaryApi(
    private val mercenaryService: MercenaryService
) {


    @PostMapping
    fun createMercenary(
        @RequestBody request: @Valid MercenaryRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<MercenaryResponse> {
        return ApiResponse.created(
            mercenaryService.createMercenary(
                request.toServiceRequest(),
                principalDetails.member
            )
        )
    }

    @GetMapping("/{mercenaryId}")
    fun getMercenary(@PathVariable mercenaryId: Long): ApiResponse<MercenaryResponse> {
        return ApiResponse.ok(mercenaryService.getMercenary(mercenaryId))
    }

    @GetMapping
    fun getMercenaries(@RequestParam page: Int, @RequestParam size: Int): ApiResponse<Page<MercenaryResponse>> {
        val pageRequest = PageRequest.of(page - 1, size)
        return ApiResponse.ok(mercenaryService.getMercenaries(pageRequest))
    }

    @DeleteMapping("/{mercenaryId}")
    fun deleteMercenary(
        @PathVariable mercenaryId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<Long> {
        return ApiResponse.ok(mercenaryService.deleteMercenary(mercenaryId, principalDetails.member))
    }
}
