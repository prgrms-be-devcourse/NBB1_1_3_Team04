package com.grepp.nbe1_3_team04.team.repository

import com.grepp.nbe1_3_team04.team.domain.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface TeamRepository : JpaRepository<Team, Long>, CustomTeamRepository {
    @Query("select t from Team t where t.isDeleted = 'false' and t.teamId = :id")
    fun findByTeamId(@Param("id") teamId: Long): Team?
}
