package com.grepp.nbe1_3_team04.stadium.repository

import com.grepp.nbe1_3_team04.stadium.domain.Stadium
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface CustomStadiumRepository {

    fun findStadiumsWithinDistanceUsingBuffer(
        latitude: Double,
        longitude: Double,
        distance: Double,
        pageable: Pageable
    ): Slice<Stadium>

}