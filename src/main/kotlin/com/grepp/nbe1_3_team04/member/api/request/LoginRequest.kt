package com.grepp.nbe1_3_team04.member.api.request

import com.grepp.nbe1_3_team04.member.service.request.LoginServiceRequest
import jakarta.validation.constraints.NotNull

data class LoginRequest(
    @field: NotNull(message = "이메일 입력하셔야 합니다.")
    val email: String?,
    @field: NotNull(message = "비밀번호를 입력하셔야 합니다.")
    val password: String?
) {
    fun toServiceRequest(): LoginServiceRequest {
        return LoginServiceRequest(email!!, password!!)
    }
}
