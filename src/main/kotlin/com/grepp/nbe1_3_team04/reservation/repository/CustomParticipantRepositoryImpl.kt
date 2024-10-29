package com.grepp.nbe1_3_team04.reservation.repository

import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.grepp.nbe1_3_team04.reservation.domain.Participant
import com.grepp.nbe1_3_team04.reservation.domain.ParticipantRole
import com.grepp.nbe1_3_team04.reservation.domain.QParticipant
import com.querydsl.jpa.impl.JPAQueryFactory

class CustomParticipantRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomParticipantRepository {

    override fun findParticipantByReservationIdAndRole(reservationId: Long): List<Participant> {
        return queryFactory
            .select(QParticipant.participant)
            .from(QParticipant.participant)
            .where(
                QParticipant.participant.isDeleted.eq(IsDeleted.FALSE)
                    .and(QParticipant.participant.reservation.reservationId.eq(reservationId))
                    .and(
                        QParticipant.participant.participantRole.eq(ParticipantRole.MEMBER)
                            .or(QParticipant.participant.participantRole.eq(ParticipantRole.ACCEPT))
                    )
            )
            .fetch()
    }

    override fun findParticipantMercenaryByReservationId(reservationId: Long): List<Participant> {
        return queryFactory
            .select(QParticipant.participant)
            .from(QParticipant.participant)
            .where(
                QParticipant.participant.isDeleted.eq(IsDeleted.FALSE)
                    .and(QParticipant.participant.reservation.reservationId.eq(reservationId))
                    .and(QParticipant.participant.participantRole.eq(ParticipantRole.PENDING))
            )
            .fetch()
    }
}
