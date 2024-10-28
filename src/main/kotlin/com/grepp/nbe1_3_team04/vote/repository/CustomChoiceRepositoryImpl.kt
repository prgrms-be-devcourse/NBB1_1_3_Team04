package com.grepp.nbe1_3_team04.vote.repository

import com.grepp.nbe1_3_team04.vote.domain.Choice
import com.grepp.nbe1_3_team04.vote.domain.QChoice
import com.grepp.nbe1_3_team04.vote.domain.QVote
import com.grepp.nbe1_3_team04.vote.domain.QVoteItem
import com.querydsl.jpa.impl.JPAQueryFactory

class CustomChoiceRepositoryImpl(private val queryFactory: JPAQueryFactory) : CustomChoiceRepository {
    override fun countByVoteItemId(voteItemId: Long): Long? {
        return queryFactory.select(QChoice.choice.count())
            .from(QChoice.choice)
            .where(QChoice.choice.voteItemId.eq(voteItemId))
            .fetchOne()
    }

    override fun findByMemberIdAndVoteId(memberId: Long, voteId: Long): List<Choice> {
        return queryFactory.select(QChoice.choice)
            .from(QChoice.choice)
            .join(QVoteItem.voteItem).on(QChoice.choice.voteItemId.eq(QVoteItem.voteItem.voteItemId))
            .join(QVoteItem.voteItem.vote, QVote.vote)
            .where(
                QChoice.choice.memberId.eq(memberId)
                    .and(QVote.vote.voteId.eq(voteId))
            )
            .fetch()
    }

    override fun findMemberIdsByVoteItemId(voteItemId: Long): List<Long> {
        return queryFactory.select(QChoice.choice.memberId)
            .from(QChoice.choice)
            .where(QChoice.choice.voteItemId.eq(voteItemId))
            .fetch()
    }

    override fun maxChoiceCountByVoteId(voteId: Long): Long? {
        return queryFactory.select(QChoice.choice.voteItemId)
            .from(QChoice.choice)
            .join(QVoteItem.voteItem).on(QChoice.choice.voteItemId.eq(QVoteItem.voteItem.voteItemId))
            .join(QVoteItem.voteItem.vote, QVote.vote)
            .where(QVote.vote.voteId.eq(voteId))
            .groupBy(QChoice.choice.voteItemId)
            .orderBy(QChoice.choice.count().desc())
            .fetchFirst()
    }
}