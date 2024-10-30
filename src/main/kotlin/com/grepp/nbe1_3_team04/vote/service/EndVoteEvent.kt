package com.grepp.nbe1_3_team04.vote.service

import java.time.LocalDateTime

data class EndVoteEvent(
    val courtId: Long,
    val memberId: Long,
    val teamId: Long,
    val matchDate: LocalDateTime,
    val memberIds: List<Long>
)
