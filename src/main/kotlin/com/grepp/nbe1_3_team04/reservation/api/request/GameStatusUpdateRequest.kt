package com.grepp.nbe1_3_team04.reservation.api.request

import com.grepp.nbe1_3_team04.reservation.domain.GameStatus
import com.grepp.nbe1_3_team04.reservation.service.request.GameStatusUpdateServiceRequest
import jakarta.validation.constraints.NotNull

data class GameStatusUpdateRequest(
    @field:NotNull(message = "게임 ID는 필수입니다.")
    val gameId: Long?,

    @field:NotNull(message = "상태는 필수입니다.")
    val status: GameStatus?
) {
    fun toServiceRequest(): GameStatusUpdateServiceRequest {
        return GameStatusUpdateServiceRequest(gameId!!, status!!)
    }
}
