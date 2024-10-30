package com.grepp.nbe1_3_team04.stadium.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.stadium.service.request.CourtDeleteServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.request.CourtRegisterServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.request.CourtUpdateServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.response.CourtDetailResponse
import com.grepp.nbe1_3_team04.stadium.service.response.CourtsResponse
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component

@Component
interface CourtService {
    fun getCourtsByStadiumId(stadiumId: Long, page: Int, sort: String): Slice<CourtsResponse>

    fun getAllCourts(page: Int, sort: String): Slice<CourtsResponse>

    fun getCourtByCourtId(courtId: Long): CourtDetailResponse

    fun registerCourt(request: CourtRegisterServiceRequest, member: Member): CourtDetailResponse

    fun updateCourt(request: CourtUpdateServiceRequest, member: Member, courtId: Long): CourtDetailResponse

    fun deleteCourt(request: CourtDeleteServiceRequest, member: Member, courtId: Long)
}