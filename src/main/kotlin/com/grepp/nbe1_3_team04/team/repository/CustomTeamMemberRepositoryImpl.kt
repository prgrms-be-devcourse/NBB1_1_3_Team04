package com.grepp.nbe1_3_team04.team.repository

import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.grepp.nbe1_3_team04.team.domain.QTeamMember.teamMember
import com.grepp.nbe1_3_team04.team.domain.TeamMember
import com.querydsl.jpa.impl.JPAQueryFactory


class CustomTeamMemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomTeamMemberRepository {

    override fun findByTeamIdAndMemberId(teamId: Long, memberId: Long): TeamMember? {
        return queryFactory
                .select(teamMember)
                .from(teamMember)
                .where(
                    teamMember.team.teamId.eq(teamId)
                        .and(teamMember.member.memberId.eq(memberId))
                        .and(teamMember.team.isDeleted.eq(IsDeleted.FALSE))
                )
                .fetchOne()
    }
}
