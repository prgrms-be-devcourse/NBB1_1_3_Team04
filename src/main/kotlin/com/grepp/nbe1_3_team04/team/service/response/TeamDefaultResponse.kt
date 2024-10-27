package com.grepp.nbe1_3_team04.team.service.response

import com.grepp.nbe1_3_team04.team.domain.Team

data class TeamDefaultResponse(
    val teamId: Long,
    val stadiumId: Long?,
    val name: String,
    val description: String?,
    val winCount: Int,
    val drawCount: Int,
    val loseCount: Int,
    val location: String?
) {
    constructor(team: Team) : this(
        requireNotNull(team.teamId),
        team.stadiumId,
        team.name,
        team.description,
        team.totalRecord.winCount,
        team.totalRecord.drawCount,
        team.totalRecord.loseCount,
        team.location
    )
}
