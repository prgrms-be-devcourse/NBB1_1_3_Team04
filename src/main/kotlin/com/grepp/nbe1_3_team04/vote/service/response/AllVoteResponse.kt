package com.grepp.nbe1_3_team04.vote.service.response

import com.grepp.nbe1_3_team04.vote.domain.Vote
import java.time.LocalDateTime

data class AllVoteResponse(
    val voteId: Long,
    val title: String,
    val endAt: LocalDateTime,
    val status: String
) {
    companion object {
        fun from(vote: Vote): AllVoteResponse {
            return AllVoteResponse(
                requireNotNull(vote.voteId),
                vote.title,
                vote.endAt,
                vote.voteStatus.name
            )
        }
    }
}
