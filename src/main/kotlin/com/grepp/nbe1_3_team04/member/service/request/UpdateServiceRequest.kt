package com.grepp.nbe1_3_team04.member.service.request

import com.grepp.nbe1_3_team04.member.domain.Gender

data class UpdateServiceRequest(
    val name: String?,
    val phoneNumber: String?,
    val gender: Gender?
)
