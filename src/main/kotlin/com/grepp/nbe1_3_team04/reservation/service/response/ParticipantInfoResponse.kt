package com.grepp.nbe1_3_team04.reservation.service.response

import com.grepp.nbe1_3_team04.reservation.domain.Participant
import com.grepp.nbe1_3_team04.reservation.domain.ParticipantRole


data class ParticipantInfoResponse(
    val memberName: String,
    val role: ParticipantRole
) {
    companion object {
        fun from(participant: Participant): ParticipantInfoResponse {
            return ParticipantInfoResponse(
                participant.member.name,
                participant.participantRole
            )
        }
    }
}
