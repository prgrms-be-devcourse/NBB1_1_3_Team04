package com.grepp.nbe1_3_team04.member.repository

import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.grepp.nbe1_3_team04.member.domain.QMember.member
import com.querydsl.jpa.impl.JPAQueryFactory

class CustomMemberRepositoryImpl(private val queryFactory: JPAQueryFactory) : CustomMemberRepository {


    override fun findMemberIdByMemberEmail(email: String): Long? {
        return queryFactory.select(member.memberId)
            .from(member)
            .where(member.email.eq(email)
                    .and(member.isDeleted.eq(IsDeleted.FALSE)))
            .fetchOne()
    }

    override fun existByEmail(email: String): Boolean {
        val count = queryFactory
            .selectOne()
            .from(member)
            .where(
                member.email.eq(email)
                    .and(member.isDeleted.eq(IsDeleted.FALSE))
            )
            .fetchFirst()

        return count != null
    }
}
