package com.grepp.nbe1_3_team04.reservation.service.request

import com.grepp.nbe1_3_team04.reservation.domain.GameStatus


data class GameStatusUpdateServiceRequest(
    val gameId: Long,
    val status: GameStatus
)
