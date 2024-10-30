package com.grepp.nbe1_3_team04.reservation.repository

import com.grepp.nbe1_3_team04.global.repository.CustomGlobalRepository
import com.grepp.nbe1_3_team04.reservation.domain.Game
import com.grepp.nbe1_3_team04.reservation.domain.GameStatus
import com.grepp.nbe1_3_team04.reservation.domain.Reservation
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface GameRepository : JpaRepository<Game, Long>, CustomGlobalRepository<Game> {
    @Query("SELECT g FROM Game g WHERE g.isDeleted = 'FALSE' AND g.secondTeamReservation = :reservation AND g.gameStatus = :status")
    fun findBySecondReservationAndStatus(
        @Param("reservation") reservation: Reservation,
        @Param("status") status: GameStatus,
        pageable: Pageable
    ): Slice<Game>

    @Query("SELECT g FROM Game g WHERE g.isDeleted = 'false' AND g.gameId = :id")
    override fun findActiveById(@Param("id") id: Long): Game?

    @Modifying
    @Query("UPDATE Game g SET g.isDeleted = 'TRUE' WHERE g.secondTeamReservation = :reservation")
    fun softDeleteBySecondTeamReservation(@Param("reservation") reservation: Reservation)

    @Query("select g.firstTeamReservation from Game g where g.isDeleted = 'false' and g.secondTeamReservation.reservationId = :reservationId")
    fun findFirstTeamReservationBySecondTeamReservationId(@Param("reservationId") secondTeamReservationId: Long): Reservation?

    @Query("select g from Game g where g.isDeleted = 'false' and g.firstTeamReservation.reservationId = :reservationId")
    fun findAllByReservationId(reservationId: Long): List<Game>
}
