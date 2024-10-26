package com.grepp.nbe1_3_team04.member.repository

import com.grepp.nbe1_3_team04.global.repository.CustomGlobalRepository
import com.grepp.nbe1_3_team04.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository : JpaRepository<Member, Long>, CustomMemberRepository { //CustomGlobalRepository {
    @Query("select m from Member m where m.isDeleted = 'false' and m.memberId = :id")
    fun findByMemberId(@Param("id") memberId: Long): Member?

    @Query("select m from Member m where m.isDeleted = 'false' and m.email = :email")
    fun findByEmail(email: String): Member?

    @Query("select m from Member m where m.isDeleted = 'false' and m.memberId = :id")
    fun findActiveById(@Param("id") memberId: Long): Member?
}