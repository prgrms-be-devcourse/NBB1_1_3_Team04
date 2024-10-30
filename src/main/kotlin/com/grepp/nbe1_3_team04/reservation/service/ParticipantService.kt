package com.grepp.nbe1_3_team04.reservation.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.reservation.service.request.ParticipantUpdateServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.response.ParticipantResponse
import org.springframework.stereotype.Component

@Component
interface ParticipantService {
    fun createMercenaryParticipant(mercenaryId: Long, member: Member): ParticipantResponse

    fun createParticipant(reservationId: Long, member: Member): ParticipantResponse

    fun deleteParticipant(reservationId: Long, member: Member): Long

    fun updateMercenaryParticipant(request: ParticipantUpdateServiceRequest, member: Member): ParticipantResponse

    fun getAcceptParticipants(reservationId: Long): List<ParticipantResponse>

    fun getParticipants(reservationId: Long): List<ParticipantResponse>

    fun getParticipantsMercenary(reservationId: Long): List<ParticipantResponse>
}
