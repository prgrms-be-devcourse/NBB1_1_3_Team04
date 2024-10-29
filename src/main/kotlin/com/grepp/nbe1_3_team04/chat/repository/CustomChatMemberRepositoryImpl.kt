package com.grepp.nbe1_3_team04.chat.repository

import com.grepp.nbe1_3_team04.chat.domain.ChatMember
import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.chat.domain.QChatMember.chatMember
import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.grepp.nbe1_3_team04.member.domain.Member
import com.querydsl.jpa.impl.JPAQueryFactory

class CustomChatMemberRepositoryImpl (
    private val queryFactory: JPAQueryFactory
) : CustomChatMemberRepository {

    override fun existByMemberAndChatroom(member: Member, chatroom: Chatroom): Boolean {
        val count = queryFactory
            .selectOne()
            .from(chatMember)
            .where(
                chatMember.isDeleted.eq(IsDeleted.FALSE)
                    .and(chatMember.chatroom.eq(chatroom))
                    .and(chatMember.member.eq(member))
            )
            .fetchFirst()

        return count != null
    }

    override fun findByChatroom(chatroom: Chatroom): List<ChatMember> {
        return queryFactory
            .select(chatMember)
            .from(chatMember)
            .where(
                chatMember.isDeleted.eq(IsDeleted.FALSE)
                    .and(chatMember.chatroom.eq(chatroom))
            )
            .fetch()
    }
}
