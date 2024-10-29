package com.grepp.nbe1_3_team04.reservation.repository

import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.grepp.nbe1_3_team04.reservation.domain.Mercenary
import com.grepp.nbe1_3_team04.reservation.domain.QMercenary.mercenary
import com.grepp.nbe1_3_team04.reservation.domain.ReservationStatus
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class CustomMercenaryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomMercenaryRepository {

    override fun findAllToPage(pageable: Pageable): Page<Mercenary> {
        val mercenaries = getMercenaryList(pageable)
        val count = count

        return PageImpl(mercenaries, pageable, count!!)
    }

    private val count: Long?
        get() = queryFactory
            .select(mercenary.count())
            .from(mercenary)
            .where(
                mercenary.isDeleted.eq(IsDeleted.FALSE)
                    .and(mercenary.reservation.reservationStatus.eq(ReservationStatus.RECRUITING))
            )
            .fetchOne()

    private fun getMercenaryList(pageable: Pageable): List<Mercenary> {
        return queryFactory
            .select(mercenary)
            .from(mercenary)
            .where(
                mercenary.isDeleted.eq(IsDeleted.FALSE)
                    .and(mercenary.reservation.reservationStatus.eq(ReservationStatus.RECRUITING))
            )
            .orderBy(mercenary.createdAt.desc())
            .offset(pageable.offset) // 페이지 번호
            .limit((pageable.pageSize + 1).toLong()) // 페이지 사이즈
            .fetch()
    }
}
