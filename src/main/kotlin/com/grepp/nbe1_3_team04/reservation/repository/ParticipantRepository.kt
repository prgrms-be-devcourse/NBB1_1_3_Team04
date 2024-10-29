package com.grepp.nbe1_3_team04.reservation.repository

import com.grepp.nbe1_3_team04.reservation.domain.Participant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface ParticipantRepository : JpaRepository<Participant, Long>,
    CustomParticipantRepository {
    @Query("select p from Participant p where p.isDeleted = 'false' and p.reservation.reservationId = :id")
    fun findParticipantsByReservationId(@Param("id") reservationId: Long): List<Participant>

    @Query("select p from Participant p where p.isDeleted = 'false' and p.reservation.reservationId = :rid and p.member.memberId = :mid")
    fun findParticipantsByReservationIdAndMemberId(
        @Param("rid") reservationId: Long,
        @Param("mid") memberId: Long
    ): Participant?

    @Query("select p from Participant p where p.isDeleted = 'false' and p.participantId = :id")
    fun findParticipantsByParticipantId(@Param("id") participantId: Long): Participant?

    @Query("select p from Participant p where p.isDeleted = 'false' and p.reservation.reservationId = :reservationId")
    fun findAllByReservationId(reservationId: Long): List<Participant>
}
