package com.grepp.nbe1_3_team04.member.repository

interface CustomMemberRepository {
    fun findMemberIdByMemberEmail(email: String?): Long?

    fun existByEmail(email: String?): Boolean
}