package com.grepp.nbe1_3_team04.vote.api.request

import com.grepp.nbe1_3_team04.vote.service.request.ChoiceCreateServiceRequest

data class ChoiceCreateRequest(
    val voteItemIds: List<Long>
) {
    fun toServiceRequest(): ChoiceCreateServiceRequest {
        return ChoiceCreateServiceRequest(
            voteItemIds
        )
    }
}
