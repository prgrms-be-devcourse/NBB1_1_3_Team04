package com.grepp.nbe1_3_team04.reservation.api.request

import com.grepp.nbe1_3_team04.reservation.service.request.MercenaryServiceRequest
import jakarta.validation.constraints.NotNull

data class MercenaryRequest(
    @field:NotNull
    val reservationId: Long?,

    val description: String?
) {
    fun toServiceRequest(): MercenaryServiceRequest {
        return MercenaryServiceRequest(reservationId!!, description)
    }
}
