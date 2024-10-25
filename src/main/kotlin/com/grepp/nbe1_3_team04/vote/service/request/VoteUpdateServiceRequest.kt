package com.grepp.nbe1_3_team04.vote.service.request

import java.time.LocalDateTime

data class VoteUpdateServiceRequest(
    val title: String,
    val endAt: LocalDateTime
)
