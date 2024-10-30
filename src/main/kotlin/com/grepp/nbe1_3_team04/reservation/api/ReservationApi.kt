package com.grepp.nbe1_3_team04.reservation.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.reservation.api.request.ReservationUpdateRequest
import com.grepp.nbe1_3_team04.reservation.service.ReservationService
import com.grepp.nbe1_3_team04.reservation.service.response.ReservationInfoDetailsResponse
import com.grepp.nbe1_3_team04.reservation.service.response.ReservationInfoResponse
import com.grepp.nbe1_3_team04.reservation.service.response.ReservationsResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Slice
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails


@RestController
@RequestMapping("/api/v1/reservation")
class ReservationApi(
    private val reservationService: ReservationService
) {


    @GetMapping("/ready")
    fun getReadyReservations(
        @RequestParam reservationId: Long,
        @RequestParam(defaultValue = "0", required = false) page: Int
    ): ApiResponse<Slice<ReservationsResponse>> {
        return ApiResponse.ok(reservationService.findReadyReservations(reservationId, page))
    }

    /**
     * 팀 경기 일정 조회
     */
    @GetMapping("/{teamId}")
    fun getTeamReservationInfo(@PathVariable teamId: Long): ApiResponse<List<ReservationInfoResponse>> {
        return ApiResponse.ok(reservationService.getTeamReservationInfo(teamId))
    }

    /**
     * 팀 경기 일정 상세 조회
     */
    @GetMapping("/details/{reservationId}")
    fun getTeamReservationInfoDetails(@PathVariable reservationId: Long): ApiResponse<ReservationInfoDetailsResponse> {
        return ApiResponse.ok(reservationService.getTeamReservationInfoDetails(reservationId))
    }

    @DeleteMapping("/{reservationId}")
    fun deleteReservation(
        @PathVariable(value = "reservationId") reservationId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<Long> {
        return ApiResponse.ok(reservationService.deleteReservation(reservationId, principalDetails.member))
    }

    @PutMapping("/update/status")
    fun updateReservationStatus(
        @RequestBody @Valid request: ReservationUpdateRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<ReservationInfoResponse> {
        return ApiResponse.ok(reservationService.changeStatus(request.toServiceRequest(), principalDetails.member))
    }
}
