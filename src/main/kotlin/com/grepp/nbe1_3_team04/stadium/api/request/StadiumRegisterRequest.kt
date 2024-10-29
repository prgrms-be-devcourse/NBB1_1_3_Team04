package com.grepp.nbe1_3_team04.stadium.api.request

import com.grepp.nbe1_3_team04.stadium.service.request.StadiumRegisterServiceRequest
import jakarta.validation.constraints.*

data class StadiumRegisterRequest(
    @field:NotBlank(message = "풋살장 이름은 필수입니다.")
    @field:Size(max = 100, message = "풋살장 이름은 최대 100자까지 가능합니다.")
    val name: String?,

    @field:NotBlank(message = "풋살장 주소는 필수입니다.")
    @field:Size(max = 100, message = "풋살장 주소는 최대 100자까지 가능합니다.")
    val address: String?,

    @field:NotBlank(message = "풋살장 연락처는 필수입니다.")
    @field:Pattern(
        regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
        message = "올바른 전화번호 형식을 입력해주세요."
    )
    val phoneNumber: String?,

    val description: String? = null,

    @field:NotNull(message = "위도 값은 필수 입력 항목입니다.")
    @field:Min(value = -90, message = "위도 값은 -90도 이상이어야 합니다.")
    @field:Max(value = 90, message = "위도 값은 90도 이하이어야 합니다.")
    val latitude: Double?,

    @field:NotNull(message = "경도 값은 필수 입력 항목입니다.")
    @field:Min(value = -180, message = "경도 값은 -180도 이상이어야 합니다.")
    @field:Max(value = 180, message = "경도 값은 180도 이하이어야 합니다.")
    val longitude: Double?
) {
    fun toServiceRequest(): StadiumRegisterServiceRequest {
        return StadiumRegisterServiceRequest(
            name = name!!,
            address = address!!,
            phoneNumber = phoneNumber!!,
            description = description,
            latitude = latitude!!,
            longitude = longitude!!
        )
    }
}
