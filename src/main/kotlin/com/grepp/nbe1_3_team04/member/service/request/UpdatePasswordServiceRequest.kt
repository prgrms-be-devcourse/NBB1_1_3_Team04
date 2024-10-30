package com.grepp.nbe1_3_team04.member.service.request

data class UpdatePasswordServiceRequest(
    val prePassword: String,
    val newPassword: String
)
