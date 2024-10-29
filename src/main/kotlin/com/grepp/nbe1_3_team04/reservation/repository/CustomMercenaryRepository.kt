package com.grepp.nbe1_3_team04.reservation.repository

import com.grepp.nbe1_3_team04.reservation.domain.Mercenary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomMercenaryRepository {
    fun findAllToPage(pageable: Pageable): Page<Mercenary>
}
