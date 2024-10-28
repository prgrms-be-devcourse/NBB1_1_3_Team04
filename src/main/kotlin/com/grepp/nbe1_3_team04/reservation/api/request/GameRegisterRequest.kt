package com.grepp.nbe1_3_team04.reservation.api.request

import com.grepp.nbe1_3_team04.reservation.service.request.GameRegisterServiceRequest
import jakarta.validation.constraints.NotNull

data class GameRegisterRequest(
    @field:NotNull(message = "신청 예약 아이디는 필수입니다.")
    val firstReservationId: Long?,

    @field:NotNull(message = "신청 받는 예약 아이디는 필수입니다.")
    val secondReservationId: Long?
) {
    fun toServiceRequest(): GameRegisterServiceRequest {
        return GameRegisterServiceRequest(firstReservationId!!, secondReservationId!!)
    }
}
