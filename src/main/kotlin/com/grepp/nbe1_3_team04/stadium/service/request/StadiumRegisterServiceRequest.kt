package com.grepp.nbe1_3_team04.stadium.service.request

data class StadiumRegisterServiceRequest(
    val name: String,
    val address: String,
    val phoneNumber: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double
)
