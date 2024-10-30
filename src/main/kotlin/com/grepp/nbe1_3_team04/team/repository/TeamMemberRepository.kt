package com.grepp.nbe1_3_team04.team.repository

import com.grepp.nbe1_3_team04.team.domain.Team
import com.grepp.nbe1_3_team04.team.domain.TeamMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TeamMemberRepository : JpaRepository<TeamMember, Long>, CustomTeamMemberRepository {
    @Query("select tm from TeamMember tm where tm.isDeleted = 'false' and tm.team = :team")
    fun findTeamMembersByTeam(@Param("team") team: Team): List<TeamMember>

    @Query("select tm from TeamMember tm where tm.isDeleted = 'false' and tm.teamMemberId = :id")
    fun findByTeamMemberId(@Param("id") teamMemberId: Long): TeamMember?

    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.member WHERE tm.isDeleted = 'false' and tm.teamMemberId = :teamMemberId")
    fun findTeamMemberWithMemberById(@Param("teamMemberId") teamMemberId: Long): TeamMember?
}
