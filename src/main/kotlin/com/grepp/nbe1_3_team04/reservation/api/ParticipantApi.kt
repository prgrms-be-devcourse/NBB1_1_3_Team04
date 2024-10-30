package com.grepp.nbe1_3_team04.reservation.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.reservation.api.request.ParticipantUpdateRequest
import com.grepp.nbe1_3_team04.reservation.service.ParticipantService
import com.grepp.nbe1_3_team04.reservation.service.response.ParticipantResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails


@RestController
@RequestMapping("/api/v1/participant")
class ParticipantApi(
    private val participantService: ParticipantService
) {


    @PostMapping("/mercenary/{mercenaryId}")
    fun applyMercenary(
        @PathVariable mercenaryId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<ParticipantResponse> {
        return ApiResponse.created(
            participantService.createMercenaryParticipant(
                mercenaryId,
                principalDetails.member
            )
        )
    }

    @PostMapping("/reservation/join/{reservationId}")
    fun joinReservation(
        @PathVariable reservationId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<ParticipantResponse> {
        return ApiResponse.created(participantService.createParticipant(reservationId, principalDetails.member))
    }

    @DeleteMapping("/reservation/leave/{reservationId}")
    fun leaveReservation(
        @PathVariable reservationId: Long,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<Long> {
        return ApiResponse.ok(participantService.deleteParticipant(reservationId, principalDetails.member))
    }

    @PutMapping
    fun updateParticipant(
        @RequestBody request: @Valid ParticipantUpdateRequest,
        @AuthenticationPrincipal principalDetails: PrincipalDetails
    ): ApiResponse<ParticipantResponse> {
        return ApiResponse.ok(
            participantService.updateMercenaryParticipant(
                request.toServiceResponse(),
                principalDetails.member
            )
        )
    }

    @GetMapping("/accept/{reservationId}")
    fun getAcceptParticipants(@PathVariable reservationId: Long): ApiResponse<List<ParticipantResponse>> {
        return ApiResponse.ok(participantService.getAcceptParticipants(reservationId))
    }

    @GetMapping("/pending/{reservationId}")
    fun getPendingParticipants(@PathVariable reservationId: Long): ApiResponse<List<ParticipantResponse>> {
        return ApiResponse.ok(participantService.getParticipantsMercenary(reservationId))
    }

    @GetMapping("/all/{reservationId}")
    fun getAllParticipants(@PathVariable reservationId: Long): ApiResponse<List<ParticipantResponse>> {
        return ApiResponse.ok(participantService.getParticipants(reservationId))
    }
}
