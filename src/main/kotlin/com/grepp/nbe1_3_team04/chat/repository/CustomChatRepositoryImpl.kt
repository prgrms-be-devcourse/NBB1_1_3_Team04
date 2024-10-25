package com.grepp.nbe1_3_team04.chat.repository

import com.grepp.nbe1_3_team04.chat.domain.Chat
import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.chat.domain.QChat
import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

class CustomChatRepositoryImpl (
    private val queryFactory: JPAQueryFactory
) : CustomChatRepository {

    override fun findChatByChatroom(chatroom: Chatroom, pageable: Pageable): Slice<ChatResponse> {
        val pageSize = pageable.pageSize
        val chats = getChatList(chatroom, pageable)
        var hasNext = false
        if (chats.size > pageSize) {
            chats.removeAt(pageSize)
            hasNext = true
        }

        val content: List<ChatResponse> = chats.map { ChatResponse(it) }

        //        Long count = getCount(chatroom);
        return SliceImpl<ChatResponse>(content, pageable, hasNext)
    }

    //Page<> 형태로 반환할 때 PageImpl에 사용
    private fun getCount(chatroom: Chatroom): Long? {
        return queryFactory
            .select(QChat.chat.count())
            .from(QChat.chat)
            .where(
                QChat.chat.isDeleted.eq(IsDeleted.FALSE)
                    .and(QChat.chat.chatRoom.eq(chatroom))
            )
            .fetchOne()
    }

    private fun getChatList(chatroom: Chatroom, pageable: Pageable): MutableList<Chat> {
        return queryFactory
            .select(QChat.chat)
            .from(QChat.chat)
            .where(
                QChat.chat.isDeleted.eq(IsDeleted.FALSE)
                    .and(QChat.chat.chatRoom.eq(chatroom))
            )
            .orderBy(QChat.chat.createdAt.desc())
            .offset(pageable.offset) // 페이지 번호
            .limit((pageable.pageSize + 1).toLong()) // 페이지 사이즈
            .fetch()
    }
}
