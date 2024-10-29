package com.grepp.nbe1_3_team04.stadium.api.request

import com.grepp.nbe1_3_team04.stadium.service.request.CourtDeleteServiceRequest
import jakarta.validation.constraints.NotNull

data class CourtDeleteRequest(
    @field:NotNull(message = "풋살장 아이디는 필수입니다.")
    val stadiumId: Long?
) {
    fun toServiceRequest(): CourtDeleteServiceRequest {
        return CourtDeleteServiceRequest(
            stadiumId = stadiumId!!
        )
    }
}