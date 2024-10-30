package com.grepp.nbe1_3_team04.reservation.api.request

import com.grepp.nbe1_3_team04.reservation.domain.ReservationStatus
import com.grepp.nbe1_3_team04.reservation.service.request.ReservationUpdateServiceRequest
import jakarta.validation.constraints.NotNull

class ReservationUpdateRequest(
    val reservationId: @NotNull Long?,
    val status: @NotNull ReservationStatus?
) {
    fun toServiceRequest(): ReservationUpdateServiceRequest {
        return ReservationUpdateServiceRequest(reservationId!!, status!!)
    }

}
