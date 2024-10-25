package com.grepp.nbe1_3_team04.vote.service.response

data class VoteItemResponse(
    val voteItemId: Long,
    val content: String,
    val memberIds: List<Long>
) {
    companion object {
        fun of(voteItemId: Long, contents: String, memberIds: List<Long>): VoteItemResponse {
            return VoteItemResponse(
                voteItemId,
                contents,
                memberIds
            )
        }
    }
}
