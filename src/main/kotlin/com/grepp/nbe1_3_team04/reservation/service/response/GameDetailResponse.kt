package com.grepp.nbe1_3_team04.reservation.service.response

import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.reservation.domain.Game
import com.grepp.nbe1_3_team04.reservation.domain.GameStatus


data class GameDetailResponse(
    val gameId: Long,
    val firstReservationId: Long,
    val secondReservationId: Long,
    val gameStatus: GameStatus
) {
    companion object {
        fun from(game: Game): GameDetailResponse {
            return GameDetailResponse(
                requireNotNull(game.gameId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
                requireNotNull(game.firstTeamReservation.reservationId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
                requireNotNull(game.secondTeamReservation.reservationId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
                game.gameStatus
            )
        }
    }
}
