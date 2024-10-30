package com.grepp.nbe1_3_team04.stadium.repository

import com.grepp.nbe1_3_team04.stadium.domain.Stadium
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Pageable

interface CustomStadiumRepository {

    fun findStadiumsByLocation(latitude: Double, longitude: Double, distance: Double, pageable: Pageable): Slice<Stadium>
}