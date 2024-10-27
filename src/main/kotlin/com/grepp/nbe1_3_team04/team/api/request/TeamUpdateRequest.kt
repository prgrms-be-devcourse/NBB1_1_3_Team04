package com.grepp.nbe1_3_team04.team.api.request

import com.grepp.nbe1_3_team04.team.service.request.TeamDefaultServiceRequest
import jakarta.validation.constraints.Null


data class TeamUpdateRequest(
    val name: String?,
    val description: String?,
    val location: String?
) {
    fun toServiceRequest(): TeamDefaultServiceRequest {
        return TeamDefaultServiceRequest(
            name = name ?: "",
            description = description ?: "",
            location = location ?: ""
        )
    }
}
