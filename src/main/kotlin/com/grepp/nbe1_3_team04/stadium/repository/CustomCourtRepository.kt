package com.grepp.nbe1_3_team04.stadium.repository

interface CustomCourtRepository {

    fun findCourtNameByCourtId(courtId: Long): String?

    fun countCourtByCourtIds(courtIds: List<Long>): Long?
}