package com.grepp.nbe1_3_team04.vote.repository

import com.grepp.nbe1_3_team04.vote.domain.VoteItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VoteItemRepository : JpaRepository<VoteItem, Long> {
    fun findByVoteVoteId(voteId: Long): List<VoteItem>
}
