package com.grepp.nbe1_3_team04.reservation.service.request

import com.grepp.nbe1_3_team04.reservation.domain.ParticipantRole


data class ParticipantUpdateServiceRequest(
    val participantId: Long,
    val role: ParticipantRole
)
