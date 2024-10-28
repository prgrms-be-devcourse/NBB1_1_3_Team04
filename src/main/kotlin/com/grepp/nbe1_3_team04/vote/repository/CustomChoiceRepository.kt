package com.grepp.nbe1_3_team04.vote.repository

import com.grepp.nbe1_3_team04.vote.domain.Choice


interface CustomChoiceRepository {
    fun countByVoteItemId(voteItemId: Long): Long?

    fun findByMemberIdAndVoteId(memberId: Long, voteId: Long): List<Choice>

    fun findMemberIdsByVoteItemId(voteItemId: Long): List<Long>

    fun maxChoiceCountByVoteId(voteId: Long): Long?
}
