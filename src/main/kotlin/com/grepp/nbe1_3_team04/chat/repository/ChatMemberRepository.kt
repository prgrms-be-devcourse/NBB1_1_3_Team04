package com.grepp.nbe1_3_team04.chat.repository

import com.grepp.nbe1_3_team04.chat.domain.ChatMember
import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatMemberRepository : JpaRepository<ChatMember, Long>, CustomChatMemberRepository {
    fun deleteByMemberAndChatroom(member: Member, chatroom: Chatroom)

    @Modifying
    @Query("UPDATE ChatMember cm SET cm.isDeleted = 'true' WHERE cm.chatroom = :chatroom")
    fun updateIsDeletedForChatroom(@Param("chatroom") chatroom: Chatroom)

    // 성능상 이슈가 존재해 QueryDSL로 대체함
    @Query("select COUNT(c.chatMemberId) > 0 from ChatMember c where c.isDeleted='false' and c.member = :member and c.chatroom = :chatroom")
    fun existsByMemberAndChatroom(@Param("member") member: Member, @Param("chatroom") chatroom: Chatroom): Boolean
}
