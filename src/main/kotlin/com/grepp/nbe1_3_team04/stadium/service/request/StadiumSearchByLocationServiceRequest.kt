package com.grepp.nbe1_3_team04.stadium.service.request

data class StadiumSearchByLocationServiceRequest(
    val latitude: Double,
    val longitude: Double,
    val distance: Double
)