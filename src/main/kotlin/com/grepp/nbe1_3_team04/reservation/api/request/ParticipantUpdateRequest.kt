package com.grepp.nbe1_3_team04.reservation.api.request

import com.grepp.nbe1_3_team04.reservation.domain.ParticipantRole
import com.grepp.nbe1_3_team04.reservation.service.request.ParticipantUpdateServiceRequest
import jakarta.validation.constraints.NotNull

data class ParticipantUpdateRequest(
    @field:NotNull
    val participantId: Long?,

    @field:NotNull
    val role: ParticipantRole?
) {
    fun toServiceResponse(): ParticipantUpdateServiceRequest {
        return ParticipantUpdateServiceRequest(participantId!!, role!!)
    }
}
