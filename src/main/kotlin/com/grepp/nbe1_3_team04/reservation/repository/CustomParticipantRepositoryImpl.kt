package com.grepp.nbe1_3_team04.reservation.repository

import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.grepp.nbe1_3_team04.reservation.domain.Participant
import com.grepp.nbe1_3_team04.reservation.domain.ParticipantRole
import com.grepp.nbe1_3_team04.reservation.domain.QParticipant.participant
import com.querydsl.jpa.impl.JPAQueryFactory

class CustomParticipantRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomParticipantRepository {

    override fun findParticipantByReservationIdAndRole(reservationId: Long): List<Participant> {
        return queryFactory
            .select(participant)
            .from(participant)
            .where(
                participant.isDeleted.eq(IsDeleted.FALSE)
                    .and(participant.reservation.reservationId.eq(reservationId))
                    .and(
                        participant.participantRole.eq(ParticipantRole.MEMBER)
                            .or(participant.participantRole.eq(ParticipantRole.ACCEPT))
                    )
            )
            .fetch()
    }

    override fun findParticipantMercenaryByReservationId(reservationId: Long): List<Participant> {
        return queryFactory
            .select(participant)
            .from(participant)
            .where(
                participant.isDeleted.eq(IsDeleted.FALSE)
                    .and(participant.reservation.reservationId.eq(reservationId))
                    .and(participant.participantRole.eq(ParticipantRole.PENDING))
            )
            .fetch()
    }
}
