package com.grepp.nbe1_3_team04.reservation.service.request

import com.grepp.nbe1_3_team04.reservation.domain.ReservationStatus

class ReservationUpdateServiceRequest(
    val reservationId: Long,
    val status: ReservationStatus
) {
}
