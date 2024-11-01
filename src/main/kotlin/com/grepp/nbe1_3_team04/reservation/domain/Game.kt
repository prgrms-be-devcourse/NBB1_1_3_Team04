package com.grepp.nbe1_3_team04.reservation.domain

import com.grepp.nbe1_3_team04.global.domain.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete

@SQLDelete(sql = "UPDATE game SET is_deleted = 'TRUE' WHERE game_id = ?")
@Entity
class Game private constructor(
    firstTeamReservation: Reservation,
    secondTeamReservation: Reservation,
    gameStatus: GameStatus
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val gameId: Long? = null

    @JoinColumn(name = "first_team_reservation_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var firstTeamReservation: Reservation = firstTeamReservation
        protected set

    @JoinColumn(name = "second_team_reservation_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    var secondTeamReservation: Reservation = secondTeamReservation
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var gameStatus: GameStatus = gameStatus
        protected set

    fun update(gameStatus: GameStatus) {
        this.gameStatus = gameStatus
    }

    companion object {
        fun create(
            firstTeamReservation: Reservation,
            secondTeamReservation: Reservation,
        ): Game {
            return Game(
                firstTeamReservation = firstTeamReservation,
                secondTeamReservation = secondTeamReservation,
                gameStatus = GameStatus.PENDING
            )
        }
    }
}
