package com.grepp.nbe1_3_team04.chat.service.event

@JvmRecord
data class ReservationDeletedEvent(
    val reservationId: Long
)