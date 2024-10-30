package com.grepp.nbe1_3_team04.team.repository

import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.grepp.nbe1_3_team04.member.domain.Gender
import com.grepp.nbe1_3_team04.member.domain.QMember.member
import com.grepp.nbe1_3_team04.team.domain.QTeamMember.teamMember
import com.querydsl.jpa.impl.JPAQueryFactory


class CustomTeamRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomTeamRepository {

    override fun countMaleByMemberId(teamId: Long): Long? {
        return queryFactory
            .select(teamMember.count())
            .from(teamMember).join(member).on(teamMember.member.memberId.eq(member.memberId))
            .where(
                member.gender.eq(Gender.MALE)
                    .and(teamMember.isDeleted.eq(IsDeleted.FALSE))
                    .and(teamMember.team.teamId.eq(teamId))
            )
            .fetchOne()
    }

    override fun countFemaleByMemberId(teamId: Long): Long? {
        return queryFactory
            .select(teamMember.count())
            .from(teamMember).join(member).on(teamMember.member.memberId.eq(member.memberId))
            .where(
                member.gender.eq(Gender.FEMALE)
                    .and(teamMember.isDeleted.eq(IsDeleted.FALSE))
                    .and(teamMember.team.teamId.eq(teamId))
            )
            .fetchOne()
    }
}
