package com.grepp.nbe1_3_team04.reservation.service

import com.grepp.nbe1_3_team04.vote.service.EndVoteEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ReservationEventHandler(
    private val reservationService: ReservationService
) {

    @EventListener
    fun handleReservationEvent(event: EndVoteEvent) {
        reservationService.createReservation(
            event.memberId,
            event.courtId,
            event.teamId,
            event.matchDate,
            event.memberIds
        )
    }
}
