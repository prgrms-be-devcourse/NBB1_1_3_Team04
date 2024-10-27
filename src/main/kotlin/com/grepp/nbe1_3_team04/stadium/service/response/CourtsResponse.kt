package com.grepp.nbe1_3_team04.stadium.service.response

import com.grepp.nbe1_3_team04.stadium.domain.Court

data class CourtsResponse(
    val courtId: Long,
    val stadiumId: Long,
    val name: String
) {
    companion object {
        fun from(court: Court): CourtsResponse {
            return CourtsResponse(
                courtId = requireNotNull(court.courtId),
                stadiumId = requireNotNull(court.stadium.stadiumId),
                name = court.name
            )
        }
    }
}