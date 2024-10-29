package com.grepp.nbe1_3_team04.reservation.service.response

import com.grepp.nbe1_3_team04.reservation.domain.Participant
import com.grepp.nbe1_3_team04.reservation.domain.ParticipantGender
import com.grepp.nbe1_3_team04.reservation.domain.Reservation
import com.grepp.nbe1_3_team04.reservation.domain.ReservationStatus
import java.time.LocalDateTime


data class ReservationInfoDetailsResponse(
    val courtName: String,
    val matchTeamName: String,
    val matchDate: LocalDateTime,
    val participants: List<ParticipantInfoResponse>,
    val gender: ParticipantGender,
    val status: ReservationStatus
) {
    companion object {
        fun of(
            reservation: Reservation,
            participants: List<Participant>,
            matchTeamName: String
        ): ReservationInfoDetailsResponse {
            val participantResponses = participants.map(ParticipantInfoResponse::from)

            return ReservationInfoDetailsResponse(
                reservation.court.name,
                matchTeamName,
                reservation.matchDate,
                participantResponses,
                reservation.gender,
                reservation.reservationStatus
            )
        }
    }
}
