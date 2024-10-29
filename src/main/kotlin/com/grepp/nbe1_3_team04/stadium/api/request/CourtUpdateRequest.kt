package com.grepp.nbe1_3_team04.stadium.api.request

import com.grepp.nbe1_3_team04.stadium.service.request.CourtUpdateServiceRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CourtUpdateRequest(
    @field:NotNull(message = "풋살장 아이디는 필수입니다.")
    val stadiumId: Long?,

    @field:NotBlank(message = "구장 이름은 필수입니다.")
    @field:Size(max = 100, message = "구장 이름은 최대 100자까지 가능합니다.")
    val name: String?,

    val description: String? = null,

    @field:NotNull(message = "시간당 요금은 필수입니다.")
    @field:PositiveOrZero(message = "요금은 음수가 될 수 없습니다.")
    val price_per_hour: BigDecimal?
) {
    fun toServiceRequest(): CourtUpdateServiceRequest {
        return CourtUpdateServiceRequest(
            stadiumId = stadiumId!!,
            name = name!!,
            description = description,
            price_per_hour = price_per_hour!!
        )
    }
}