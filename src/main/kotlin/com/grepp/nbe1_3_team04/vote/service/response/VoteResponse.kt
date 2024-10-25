package com.grepp.nbe1_3_team04.vote.service.response

import com.grepp.nbe1_3_team04.vote.domain.Vote
import java.time.LocalDateTime

@JvmRecord
data class VoteResponse(
    val voteId: Long,
    val title: String,
    val endAt: LocalDateTime,
    val voteStatus: String,
    val choices: List<VoteItemResponse>
) {
    companion object {
        fun of(vote: Vote, choices: List<VoteItemResponse>): VoteResponse {
            return VoteResponse(
                requireNotNull(vote.voteId),
                vote.title,
                vote.endAt,
                vote.voteStatus.name,
                choices
            )
        }
    }
}
