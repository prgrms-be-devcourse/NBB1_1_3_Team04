package com.grepp.nbe1_3_team04.reservation.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.reservation.api.request.GameRegisterRequest
import com.grepp.nbe1_3_team04.reservation.api.request.GameStatusUpdateRequest
import com.grepp.nbe1_3_team04.reservation.service.GameService
import com.grepp.nbe1_3_team04.reservation.service.response.GameDetailResponse
import jakarta.validation.Valid
import org.springframework.data.domain.Slice
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails


@RestController
@RequestMapping("/api/v1/game")
class GameApi(
    private val gameService: GameService
) {

    @PostMapping("/register")
    fun registerGame(
        @AuthenticationPrincipal currentUser: PrincipalDetails,
        @RequestBody request: @Valid GameRegisterRequest
    ): ApiResponse<GameDetailResponse> {
        return ApiResponse.created(gameService.registerGame(currentUser.member, request.toServiceRequest()))
    }

    @GetMapping("/game")
    fun getPendingGames(
        @AuthenticationPrincipal currentUser: PrincipalDetails,
        @RequestParam(defaultValue = "0", required = false) page: Int,
        @RequestParam reservationId: Long
    ): ApiResponse<Slice<GameDetailResponse>> {
        return ApiResponse.ok(gameService.findPendingGames(currentUser.member, reservationId, page))
    }

    @PutMapping("/status")
    fun updateGameStatus(
        @AuthenticationPrincipal currentUser: PrincipalDetails,
        @RequestBody request: @Valid GameStatusUpdateRequest
    ): ApiResponse<String> {
        return ApiResponse.ok(gameService.updateGameStatus(currentUser.member, request.toServiceRequest()))
    }
}
