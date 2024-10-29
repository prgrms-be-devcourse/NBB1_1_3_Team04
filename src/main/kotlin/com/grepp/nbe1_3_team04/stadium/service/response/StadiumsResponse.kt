package com.grepp.nbe1_3_team04.stadium.service.response

import com.grepp.nbe1_3_team04.stadium.domain.Stadium

data class StadiumsResponse(
    val stadiumId: Long,
    val name: String,
    val address: String
) {
    companion object {
        fun from(stadium: Stadium): StadiumsResponse {
            return StadiumsResponse(
                stadiumId = requireNotNull(stadium.stadiumId),
                name = stadium.name,
                address = stadium.address
            )
        }
    }
}