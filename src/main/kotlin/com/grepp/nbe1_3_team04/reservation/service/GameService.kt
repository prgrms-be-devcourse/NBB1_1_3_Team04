package com.grepp.nbe1_3_team04.reservation.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.reservation.service.request.GameRegisterServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.request.GameStatusUpdateServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.response.GameDetailResponse
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
interface GameService {
    fun registerGame(member: Member, request: GameRegisterServiceRequest): GameDetailResponse

    fun updateGameStatus(member: Member, request: GameStatusUpdateServiceRequest): String

    fun findPendingGames(member: Member, reservationId: Long, page: Int): Slice<GameDetailResponse>
}
