package com.grepp.nbe1_3_team04.reservation.service.response

import com.grepp.nbe1_3_team04.reservation.domain.Reservation
import com.grepp.nbe1_3_team04.reservation.domain.ReservationStatus
import java.time.LocalDateTime


data class ReservationInfoResponse(
    val courtName: String,
    val matchDate: LocalDateTime,
    val status: ReservationStatus
) {
    companion object {
        fun from(reservation: Reservation): ReservationInfoResponse {
            return ReservationInfoResponse(
                reservation.court.getName(),
                reservation.matchDate,
                reservation.reservationStatus
            )
        }
    }
}
