package com.grepp.nbe1_3_team04.team.service.response

import com.grepp.nbe1_3_team04.team.domain.Team

//팀 정보
data class TeamInfoResponse(
    val name: String,
    val description: String?,
    val location: String?,
    val winCount: Int,
    val loseCount: Int,
    val drawCount: Int,
    val evaluation: List<String>?,
    val maleCount: Long,
    val femaleCount: Long
) {
    constructor(team: Team, evaluation: List<String>, maleCount: Long, femaleCount: Long) : this(
        team.name,
        team.description,
        team.location,
        team.totalRecord.winCount,
        team.totalRecord.loseCount,
        team.totalRecord.drawCount,
        evaluation,
        maleCount,
        femaleCount
    )
}
