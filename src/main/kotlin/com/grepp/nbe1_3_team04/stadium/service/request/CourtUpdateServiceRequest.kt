package com.grepp.nbe1_3_team04.stadium.service.request

import java.math.BigDecimal

data class CourtUpdateServiceRequest(
    val stadiumId: Long,
    val name: String,
    val description: String?,
    val price_per_hour: BigDecimal
)