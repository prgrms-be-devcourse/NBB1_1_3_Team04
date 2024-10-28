package com.grepp.nbe1_3_team04.team.api.request

import com.grepp.nbe1_3_team04.team.service.request.TeamMemberServiceRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Email

data class TeamMemberRequest(
    @field: Valid
    val emails: List<@Email String>
) {
    fun toServiceRequest(): TeamMemberServiceRequest {
        return TeamMemberServiceRequest(emails)
    }
}
