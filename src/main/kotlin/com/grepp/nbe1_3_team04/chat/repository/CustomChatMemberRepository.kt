package com.grepp.nbe1_3_team04.chat.repository

import com.grepp.nbe1_3_team04.chat.domain.ChatMember
import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.member.domain.Member

interface CustomChatMemberRepository {
    fun existByMemberAndChatroom(member: Member, chatroom: Chatroom): Boolean

    fun findByChatroom(chatroom: Chatroom): List<ChatMember>
}
