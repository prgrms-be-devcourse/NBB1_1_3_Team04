package com.grepp.nbe1_3_team04.reservation.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.reservation.service.request.MercenaryServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.response.MercenaryResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
interface MercenaryService {
    fun createMercenary(request: MercenaryServiceRequest, member: Member): MercenaryResponse

    fun getMercenary(mercenaryId: Long): MercenaryResponse

    fun getMercenaries(pageable: Pageable): Page<MercenaryResponse>

    fun deleteMercenary(mercenaryId: Long, member: Member): Long
}
