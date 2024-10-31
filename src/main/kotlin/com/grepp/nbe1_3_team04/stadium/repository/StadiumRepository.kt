package com.grepp.nbe1_3_team04.stadium.repository

import com.grepp.nbe1_3_team04.global.repository.CustomGlobalRepository
import com.grepp.nbe1_3_team04.stadium.domain.Stadium
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface StadiumRepository : JpaRepository<Stadium, Long>, CustomStadiumRepository, CustomGlobalRepository<Stadium> {

    @Query("SELECT s FROM Stadium s WHERE s.isDeleted = 'false' AND s.stadiumId = :id")
    fun findByStadiumId(@Param("id") id: Long): Stadium?

    @Query("SELECT s FROM Stadium s WHERE s.isDeleted = 'false' AND s.stadiumId = :id")
    override fun findActiveById(@Param("id") id: Long): Stadium?

    @Query("SELECT s FROM Stadium s WHERE s.isDeleted = 'false' AND s.name = :name")
    fun findActiveByName(@Param("name") name: String): Stadium?

    @Query("SELECT s FROM Stadium s WHERE s.isDeleted = 'false'")
    fun findAllActiveStadiums(pageable: Pageable): Slice<Stadium>

    @Query("SELECT s FROM Stadium s WHERE s.isDeleted = 'false' AND LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun findByNameContainingIgnoreCase(@Param("query") query: String, pageable: Pageable): Slice<Stadium>

    @Query("SELECT s FROM Stadium s WHERE s.isDeleted = 'false' AND LOWER(s.address) LIKE LOWER(CONCAT('%', :address, '%'))")
    fun findByAddressContainingIgnoreCase(@Param("address") address: String, pageable: Pageable): Slice<Stadium>
}