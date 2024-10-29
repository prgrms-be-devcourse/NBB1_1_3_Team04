package com.grepp.nbe1_3_team04.stadium.repository

import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.grepp.nbe1_3_team04.stadium.domain.Stadium
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberTemplate
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.domain.Pageable

import com.grepp.nbe1_3_team04.stadium.domain.QStadium.stadium

class CustomStadiumRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomStadiumRepository {

    override fun findStadiumsByLocation(
        latitude: Double,
        longitude: Double,
        distance: Double,
        pageable: Pageable
    ): Slice<Stadium> {
        val haversineDistance = calculateHaversineDistance(latitude, longitude)

        val stadiums = fetchStadiumsByLocation(haversineDistance, distance, pageable)

        return createSlice(stadiums, pageable)
    }

    private fun calculateHaversineDistance(
        latitude: Double,
        longitude: Double
    ): NumberTemplate<Double> {
        return Expressions.numberTemplate(
            Double::class.java,
            "(6371 * acos(cos(radians({0})) * cos(radians({1})) * cos(radians({2}) - radians({3})) + sin(radians({0})) * sin(radians({1}))))",
            latitude, stadium.position.latitude, stadium.position.longitude, longitude
        )
    }

    private fun fetchStadiumsByLocation(
        haversineDistance: NumberTemplate<Double>,
        distance: Double,
        pageable: Pageable
    ): List<Stadium> {
        return queryFactory
            .selectFrom(stadium)
            .where(
                haversineDistance.loe(distance)
                    .and(stadium.isDeleted.eq(IsDeleted.FALSE))
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize + 1L)
            .fetch()
    }

    private fun createSlice(stadiums: List<Stadium>, pageable: Pageable): Slice<Stadium> {
        val hasNext = stadiums.size > pageable.pageSize
        val content = if (hasNext) stadiums.subList(0, pageable.pageSize) else stadiums
        return SliceImpl(content, pageable, hasNext)
    }
}