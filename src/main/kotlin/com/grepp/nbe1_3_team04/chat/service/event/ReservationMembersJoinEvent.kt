package com.grepp.nbe1_3_team04.chat.service.event

import com.grepp.nbe1_3_team04.reservation.domain.Participant


data class ReservationMembersJoinEvent(
    val members: List<Participant>,
    val reservationId: Long
)
