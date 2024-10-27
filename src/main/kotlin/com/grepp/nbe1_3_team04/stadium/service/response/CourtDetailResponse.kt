package com.grepp.nbe1_3_team04.stadium.service.response

import com.grepp.nbe1_3_team04.stadium.domain.Court
import java.math.BigDecimal

data class CourtDetailResponse(
    val courtId: Long,
    val stadiumId: Long,
    val name: String,
    val description: String?,
    val pricePerHour: BigDecimal
) {
    companion object {
        fun from(court: Court): CourtDetailResponse {
            return CourtDetailResponse(
                courtId = requireNotNull(court.courtId),
                stadiumId = requireNotNull(court.stadium.stadiumId),
                name = court.name,
                description = court.description,
                pricePerHour = court.pricePerHour
            )
        }
    }
}