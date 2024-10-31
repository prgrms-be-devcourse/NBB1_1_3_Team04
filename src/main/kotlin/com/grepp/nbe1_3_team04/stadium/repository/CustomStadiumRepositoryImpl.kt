package com.grepp.nbe1_3_team04.stadium.repository

import com.grepp.nbe1_3_team04.stadium.domain.Stadium
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
class CustomStadiumRepositoryImpl(
    @Autowired private val entityManager: EntityManager
) : CustomStadiumRepository {
    override fun findStadiumsWithinDistanceUsingBuffer(
        latitude: Double,
        longitude: Double,
        distance: Double,
        pageable: Pageable
    ): Slice<Stadium> {
        val query = """
            SELECT s 
            FROM Stadium s 
            WHERE s.isDeleted = 'false' AND ST_Contains(
                ST_Buffer(ST_GeomFromText(:point, 4326), :distance), 
                s.location
            ) = true
            ORDER BY ${pageable.sort.joinToString(", ") { it.property + " " + it.direction.name }}
        """

        val pointWKT = "POINT($longitude $latitude)"

        val typedQuery = entityManager.createQuery(query, Stadium::class.java)
            .setParameter("point", pointWKT)
            .setParameter("distance", distance)
            .setFirstResult(pageable.offset.toInt())
            .setMaxResults(pageable.pageSize)

        val results = typedQuery.resultList
        return PageImpl(results, pageable, results.size.toLong())
    }
}