package com.grepp.nbe1_3_team04.reservation.repository

import com.grepp.nbe1_3_team04.reservation.domain.Participant


interface CustomParticipantRepository {
    fun findParticipantByReservationIdAndRole(reservationId: Long): List<Participant>

    fun findParticipantMercenaryByReservationId(reservationId: Long): List<Participant>
}
