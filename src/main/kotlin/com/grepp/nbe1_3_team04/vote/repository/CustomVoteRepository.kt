package com.grepp.nbe1_3_team04.vote.repository

import com.grepp.nbe1_3_team04.vote.domain.Vote
import java.util.*


interface CustomVoteRepository {
    fun findNotDeletedVoteById(id: Long): Vote?

    fun findOpenedVotes(): List<Vote>

    fun choiceMemberCountByVoteId(voteId: Long): Long?

    fun findRecentlyVoteByTeamId(teamId: Long): Vote?

    fun findAllByTeamId(teamId: Long): List<Vote>
}
