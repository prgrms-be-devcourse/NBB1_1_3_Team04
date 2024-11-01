package com.grepp.nbe1_3_team04.member.api.request

import com.grepp.nbe1_3_team04.member.api.request.validation.PasswordMatches
import com.grepp.nbe1_3_team04.member.service.request.UpdatePasswordServiceRequest
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

@PasswordMatches(passwordField = "newPassword", passwordConfirmField = "newPasswordConfirm")
data class UpdatePasswordRequest(
    @field: NotNull(message = "이전 비밀번호를 입력하셔야 합니다.")
    val prePassword: String?,
    @field: Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$",
        message = "비밀번호는 8~16자 사이  숫자, 영문자, 특수 문자를 각각 최소 한 개 이상 포함하여야 합니다."
    )
    val newPassword: String?,

    @field: Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$",
        message = "비밀번호는 8~16자 사이  숫자, 영문자, 특수 문자를 각각 최소 한 개 이상 포함하여야 합니다."
    )
    val newPasswordConfirm:String?
) {
    fun toServiceRequest(): UpdatePasswordServiceRequest {
        return UpdatePasswordServiceRequest(prePassword!!, newPassword!!)
    }
}
