package com.grepp.nbe1_3_team04.reservation.service.response

import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.reservation.domain.Participant
import com.grepp.nbe1_3_team04.reservation.domain.ParticipantRole


data class ParticipantResponse(
    val participantId: Long,
    val reservationId: Long,
    val role: ParticipantRole,
    val memberInfo: ParticipantMemberInfo
) {
    constructor(participant: Participant) : this(
        requireNotNull(participant.participantId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
        requireNotNull(participant.reservation.reservationId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
        participant.participantRole,
        ParticipantMemberInfo(participant.member)
    )
}
