package com.grepp.nbe1_3_team04.stadium.api.request

import com.grepp.nbe1_3_team04.stadium.service.request.StadiumSearchByLocationServiceRequest
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class StadiumSearchByLocationRequest(
    @field:NotNull(message = "위도 값은 필수 입력 항목입니다.")
    @field:Min(value = -90, message = "위도 값은 -90도 이상이어야 합니다.")
    @field:Max(value = 90, message = "위도 값은 90도 이하이어야 합니다.")
    val latitude: Double?,

    @field:NotNull(message = "경도 값은 필수 입력 항목입니다.")
    @field:Min(value = -180, message = "경도 값은 -180도 이상이어야 합니다.")
    @field:Max(value = 180, message = "경도 값은 180도 이하이어야 합니다.")
    val longitude: Double?,

    @field:NotNull(message = "거리 값은 필수 입력 항목입니다.")
    @field:Min(value = 0, message = "거리는 0 이상이어야 합니다.")
    val distance: Double?
) {
    fun toServiceRequest(): StadiumSearchByLocationServiceRequest {
        return StadiumSearchByLocationServiceRequest(
            latitude = latitude!!,
            longitude = longitude!!,
            distance = distance!!
        )
    }
}