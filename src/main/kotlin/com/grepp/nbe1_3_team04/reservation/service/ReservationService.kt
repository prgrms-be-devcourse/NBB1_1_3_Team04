package com.grepp.nbe1_3_team04.reservation.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.reservation.service.request.ReservationUpdateServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.response.ReservationInfoDetailsResponse
import com.grepp.nbe1_3_team04.reservation.service.response.ReservationInfoResponse
import com.grepp.nbe1_3_team04.reservation.service.response.ReservationsResponse
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
interface ReservationService {
    fun findReadyReservations(reservationId: Long, page: Int): Slice<ReservationsResponse>

    fun createReservation(
        memberId: Long,
        courtId: Long,
        teamId: Long,
        matchDate: LocalDateTime,
        memberIds: List<Long>
    )

    fun getTeamReservationInfo(teamId: Long): List<ReservationInfoResponse>

    fun getTeamReservationInfoDetails(reservationId: Long): ReservationInfoDetailsResponse

    fun deleteReservation(reservationId: Long, member: Member): Long

    fun changeStatus(request: ReservationUpdateServiceRequest, member: Member): ReservationInfoResponse
}