package com.grepp.nbe1_3_team04.stadium.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.stadium.service.request.StadiumRegisterServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.request.StadiumSearchByLocationServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.request.StadiumUpdateServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.response.StadiumDetailResponse
import com.grepp.nbe1_3_team04.stadium.service.response.StadiumsResponse
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
interface StadiumService {
    fun getStadiumList(page: Int, sort: String): Slice<StadiumsResponse>

    fun getStadiumDetail(id: Long): StadiumDetailResponse

    fun getStadiumsByName(query: String, page: Int, sort: String): Slice<StadiumsResponse>

    fun getStadiumsByAddress(address: String, page: Int, sort: String): Slice<StadiumsResponse>

    fun getStadiumsWithinDistance(
        request: StadiumSearchByLocationServiceRequest,
        page: Int,
        sort: String
    ): Slice<StadiumsResponse>

    fun registerStadium(request: StadiumRegisterServiceRequest, member: Member): StadiumDetailResponse

    fun updateStadium(
        request: StadiumUpdateServiceRequest,
        member: Member,
        stadiumId: Long
    ): StadiumDetailResponse

    fun deleteStadium(member: Member, stadiumId: Long)
}