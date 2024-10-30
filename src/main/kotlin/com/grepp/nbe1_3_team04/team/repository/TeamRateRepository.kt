package com.grepp.nbe1_3_team04.team.repository

import com.grepp.nbe1_3_team04.team.domain.Team
import com.grepp.nbe1_3_team04.team.domain.TeamRate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRateRepository : JpaRepository<TeamRate, Long> {
    fun findEvaluationsByTeam(team: Team): List<TeamRate>
}
