package com.grepp.nbe1_3_team04.team.repository

interface CustomTeamRepository {
    fun countMaleByMemberId(teamId: Long): Long

    fun countFemaleByMemberId(teamId: Long): Long
}
