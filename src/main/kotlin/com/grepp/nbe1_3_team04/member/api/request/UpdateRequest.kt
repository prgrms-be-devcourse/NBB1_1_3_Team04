package com.grepp.nbe1_3_team04.member.api.request

import com.grepp.nbe1_3_team04.member.domain.Gender
import com.grepp.nbe1_3_team04.member.service.request.UpdateServiceRequest
import jakarta.validation.constraints.Pattern

data class UpdateRequest(
    val name: String?,
    @field: Pattern(
        regexp = "^010-\\d{3,4}-\\d{4}$",
        message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다."
    )
    val phoneNumber: String?,
    val gender: Gender?
) {
    fun toServiceRequest(): UpdateServiceRequest {
        return UpdateServiceRequest(name, phoneNumber, gender)
    }
}
