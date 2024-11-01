package com.grepp.nbe1_3_team04.reservation.domain

import com.grepp.nbe1_3_team04.global.domain.BaseEntity
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.stadium.domain.Court
import com.grepp.nbe1_3_team04.team.domain.Team
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@SQLDelete(sql = "UPDATE reservation SET is_deleted = 'TRUE' WHERE reservation_id = ?")
@Entity
class Reservation private constructor(
    court: Court,
    member: Member,
    team: Team,
    matchDate: LocalDateTime,
    reservationStatus: ReservationStatus,
    gender: ParticipantGender
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val reservationId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    var court: Court = court
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member = member
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    var team: Team = team
        protected set

    @Column(nullable = false)
    var matchDate: LocalDateTime = matchDate
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var reservationStatus: ReservationStatus = reservationStatus
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var gender: ParticipantGender = gender
        protected set

    fun updateStatus(reservationStatus: ReservationStatus) {
        this.reservationStatus = reservationStatus
    }

    fun cancel() {
        this.reservationStatus = ReservationStatus.CANCELLED
    }

    fun confirm() {
        this.reservationStatus = ReservationStatus.CONFIRMED
    }

    fun checkReservationOwner(memberId: Long) {
        if (member.memberId == memberId) {
            return
        }
        throw IllegalArgumentException("회의를 예약한 사람만 수정 및 삭제할 수 있습니다.")
    }

    companion object {
        fun create(
            court: Court,
            member: Member,
            team: Team,
            matchDate: LocalDateTime,
            reservationStatus: ReservationStatus,
            gender: ParticipantGender
        ): Reservation {
            return Reservation(
                court = court,
                member = member,
                team = team,
                matchDate = matchDate,
                reservationStatus = reservationStatus,
                gender = gender
            )
        }

        fun createReadyReservation(
            court: Court,
            member: Member,
            team: Team,
            gender: ParticipantGender,
            matchDate: LocalDateTime
        ): Reservation {
            return Reservation(
                court = court,
                member = member,
                team = team,
                matchDate = matchDate,
                reservationStatus = ReservationStatus.READY,
                gender = gender
            )
        }

        fun createRecruitReservation(
            court: Court,
            member: Member,
            team: Team,
            gender: ParticipantGender,
            matchDate: LocalDateTime
        ): Reservation {
            return Reservation(
                court = court,
                member = member,
                team = team,
                matchDate = matchDate,
                reservationStatus = ReservationStatus.RECRUITING,
                gender = gender
            )
        }
    }
}
