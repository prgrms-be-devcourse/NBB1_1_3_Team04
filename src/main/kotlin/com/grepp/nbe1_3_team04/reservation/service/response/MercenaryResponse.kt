package com.grepp.nbe1_3_team04.reservation.service.response

import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.reservation.domain.Mercenary


data class MercenaryResponse(
    val mercenaryId: Long,
    val reservationId: Long,
    val description: String?
) {
    constructor(mercenary: Mercenary) : this(
        requireNotNull(mercenary.mercenaryId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
        requireNotNull(mercenary.reservation.reservationId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
        mercenary.description
    )
}
