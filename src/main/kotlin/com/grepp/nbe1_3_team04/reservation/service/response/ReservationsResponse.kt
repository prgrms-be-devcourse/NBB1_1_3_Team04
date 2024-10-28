package com.grepp.nbe1_3_team04.reservation.service.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.reservation.domain.ParticipantGender
import com.grepp.nbe1_3_team04.reservation.domain.Reservation
import java.time.LocalDateTime

data class ReservationsResponse(
    val reservationId: Long,
    val courtId: Long,
    val memberId: Long,
    val teamId: Long,
    @field:JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss"
    ) @param:JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss"
    ) val matchDate: LocalDateTime,
    val gender: ParticipantGender
) {
    companion object {
        fun from(reservation: Reservation): ReservationsResponse {
            return ReservationsResponse(
                requireNotNull(reservation.reservationId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
                reservation.court.getCourtId(),
                requireNotNull(reservation.member.memberId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
                reservation.team.getTeamId(),
                reservation.matchDate,
                reservation.gender
            )
        }
    }
}
