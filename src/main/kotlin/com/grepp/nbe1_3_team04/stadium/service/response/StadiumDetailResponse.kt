package com.grepp.nbe1_3_team04.stadium.service.response

import com.grepp.nbe1_3_team04.stadium.domain.Stadium

data class StadiumDetailResponse(
    val stadiumId: Long,
    val memberId: Long,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun from(stadium: Stadium): StadiumDetailResponse {
            val coordinate = stadium.location.coordinate
            return StadiumDetailResponse(
                stadiumId = requireNotNull(stadium.stadiumId),
                memberId = requireNotNull(stadium.member.memberId),
                name = stadium.name,
                address = stadium.address,
                phoneNumber = stadium.phoneNumber,
                description = stadium.description,
                latitude = coordinate.y,
                longitude = coordinate.x
            )
        }
    }
}