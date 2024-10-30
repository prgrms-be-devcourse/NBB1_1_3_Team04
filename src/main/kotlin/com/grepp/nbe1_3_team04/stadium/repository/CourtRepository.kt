package com.grepp.nbe1_3_team04.stadium.repository

import com.grepp.nbe1_3_team04.global.repository.CustomGlobalRepository
import com.grepp.nbe1_3_team04.stadium.domain.Court
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CourtRepository : JpaRepository<Court, Long>, CustomGlobalRepository<Court>, CustomCourtRepository {

    @Query("SELECT c FROM Court c WHERE c.isDeleted = 'false' AND c.stadium.stadiumId = :stadiumId")
    fun findByStadium_StadiumId(@Param("stadiumId") stadiumId: Long, pageable: Pageable): Slice<Court>

    @Query("SELECT c FROM Court c WHERE c.isDeleted = 'false'")
    fun findAllActive(pageable: Pageable): Slice<Court>

    @Query("SELECT c FROM Court c WHERE c.isDeleted = 'false' AND c.courtId = :id")
    fun findByCourtId(@Param("id") id: Long): Court?

    @Query("SELECT c FROM Court c WHERE c.isDeleted = 'false' AND c.courtId = :id")
    override fun findActiveById(@Param("id") id: Long): Court?

    @Query("SELECT c FROM Court c WHERE c.isDeleted = 'false' AND c.stadium.stadiumId = :id")
    fun findActiveByStadiumId(@Param("id") id: Long): List<Court>
}