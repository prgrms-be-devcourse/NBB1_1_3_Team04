package com.grepp.nbe1_3_team04.stadium.repository

import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.grepp.nbe1_3_team04.stadium.domain.QCourt.court
import com.querydsl.jpa.impl.JPAQueryFactory

class CustomCourtRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomCourtRepository {

    override fun findCourtNameByCourtId(courtId: Long): String? {
        return queryFactory.select(court.name)
            .from(court)
            .where(
                court.courtId.eq(courtId)
                    .and(court.isDeleted.eq(IsDeleted.FALSE))
            )
            .fetchOne()
    }

    override fun countCourtByCourtIds(courtIds: List<Long>): Long? {
        return queryFactory.select(court.count())
            .from(court)
            .where(
                court.courtId.`in`(courtIds)
                    .and(court.isDeleted.eq(IsDeleted.FALSE))
            )
            .fetchOne()
    }
}