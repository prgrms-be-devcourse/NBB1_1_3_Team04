package com.grepp.nbe1_3_team04.team.api.request

import com.grepp.nbe1_3_team04.team.service.request.TeamDefaultServiceRequest
import jakarta.validation.constraints.NotNull

data class TeamCreateRequest(
    @field:NotNull(message = "팀 명은 필수입니다.")
    val name: String?,
    val description: String?,
    val location: String?
) {
    fun toServiceRequest(): TeamDefaultServiceRequest {
        return TeamDefaultServiceRequest(
            name = name!!,
            description = description,
            location = location
        )
    }
}
