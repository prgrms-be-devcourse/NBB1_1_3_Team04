package com.grepp.nbe1_3_team04.reservation.repository

import com.grepp.nbe1_3_team04.global.repository.CustomGlobalRepository
import com.grepp.nbe1_3_team04.reservation.domain.Reservation
import com.grepp.nbe1_3_team04.reservation.domain.ReservationStatus
import com.grepp.nbe1_3_team04.stadium.domain.Court
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface ReservationRepository : JpaRepository<Reservation, Long>, CustomGlobalRepository<Reservation> {
    @Query("SELECT r FROM Reservation r WHERE r.isDeleted = 'false' AND r.reservationId = :id")
    override fun findActiveById(@Param("id") id: Long): Reservation?

    @Query("SELECT r FROM Reservation r WHERE r.isDeleted = 'FALSE' AND r.matchDate = :matchDate AND r.court = :court AND r.reservationStatus = :reservationStatus AND r.reservationId != :id")
    fun findByMatchDateAndCourtAndReservationStatus(
        @Param("id") id: Long,
        @Param("matchDate") matchDate: LocalDateTime,
        @Param("court") court: Court,
        @Param("reservationStatus") reservationStatus: ReservationStatus,
        pageable: Pageable
    ): Slice<Reservation>

    @Query("select r from Reservation r where r.isDeleted = 'false' and r.reservationId = :id")
    fun findByReservationId(@Param("id") reservationId: Long): Reservation?

    @Query("select r from Reservation r where r.isDeleted = 'false' and r.team.teamId = :teamId")
    fun findByTeamTeamId(@Param("teamId") teamId: Long): List<Reservation>
}
