package com.grepp.nbe1_3_team04.member.api.request

import com.grepp.nbe1_3_team04.member.api.request.validation.PasswordMatches
import com.grepp.nbe1_3_team04.member.domain.Gender
import com.grepp.nbe1_3_team04.member.domain.LoginProvider
import com.grepp.nbe1_3_team04.member.domain.MemberRole
import com.grepp.nbe1_3_team04.member.domain.TermsAgreed
import com.grepp.nbe1_3_team04.member.service.request.JoinServiceRequest
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

@PasswordMatches(passwordField = "password", passwordConfirmField = "passwordConfirm")
data class JoinRequest(
    @field: NotBlank(message = "이메일은 필수입니다.") @Email(message = "형식이 이메일이어야 합니다.")
    val email:String?,
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$",
        message = "비밀번호는 8~16자 사이  숫자, 영문자, 특수 문자를 각각 최소 한 개 이상 포함하여야 합니다."
    )
    val password: String?,
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,16}$",
        message = "비밀번호는 8~16자 사이  숫자, 영문자, 특수 문자를 각각 최소 한 개 이상 포함하여야 합니다."
    )
    val passwordConfirm: String?,
    @field: NotBlank(message = "이름이 빈 칸 입니다.")
    val name:  String?,
    @Pattern(
        regexp = "^010-\\d{3,4}-\\d{4}$",
        message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다."
    )
    @field: NotBlank(message = "전화번호는 필수입니다.")
    val phoneNumber: String?,
    val loginProvider: LoginProvider,
    val snsId: String,
    val gender: Gender,
    val memberRole: MemberRole,
    @field: NotNull(message = "체크해야 합니다.")
    val termsAgree: TermsAgreed?
) {
    fun toServiceRequest(): JoinServiceRequest {
        return JoinServiceRequest(
            email!!,
            password,
            name!!,
            phoneNumber!!,
            loginProvider,
            snsId,
            gender,
            memberRole,
            termsAgree!!
        )
    }
}
