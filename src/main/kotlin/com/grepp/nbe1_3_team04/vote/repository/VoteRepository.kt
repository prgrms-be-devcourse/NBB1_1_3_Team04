package com.grepp.nbe1_3_team04.vote.repository

import com.grepp.nbe1_3_team04.vote.domain.Vote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VoteRepository : JpaRepository<Vote, Long>, CustomVoteRepository
