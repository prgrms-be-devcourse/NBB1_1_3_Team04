package com.grepp.nbe1_3_team04.chat.repository

import com.grepp.nbe1_3_team04.chat.domain.Chat
import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.chat.domain.QChat.chat
import com.grepp.nbe1_3_team04.chat.service.response.ChatResponse
import com.grepp.nbe1_3_team04.global.domain.IsDeleted
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import java.time.LocalDateTime

class CustomChatRepositoryImpl (
    private val queryFactory: JPAQueryFactory
) : CustomChatRepository {

    override fun findChatByChatroomPage(chatroom: Chatroom, pageable: Pageable, cursor: LocalDateTime?): Slice<ChatResponse> {
        val pageSize = pageable.pageSize
        val chats = getChatroomList(chatroom, pageable, cursor)
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
            .select(chat.count())
            .from(chat)
            .where(
                chat.isDeleted.eq(IsDeleted.FALSE)
                    .and(chat.chatroom.eq(chatroom))
            )
            .fetchOne()
    }

    private fun getChatroomList(chatroom: Chatroom, pageable: Pageable, cursor: LocalDateTime?): MutableList<Chat> {
        return queryFactory
            .select(chat)
            .from(chat)
            .where(
                chat.isDeleted.eq(IsDeleted.FALSE)
                    .and(chat.chatroom.eq(chatroom))
                    .let {
                        // 커서가 있는 경우, createdAt이 커서보다 작은 항목만 가져옴
                        cursor?.let { cur -> it.and(chat.createdAt.lt(cur)) } ?: it
                    }
            )
            .orderBy(chat.createdAt.desc())
            .limit((pageable.pageSize + 1).toLong()) // 페이지 사이즈
            .fetch()
    }

    override fun findChatByChatroomList(chatroom: Chatroom, limit: Int, cursor: LocalDateTime?): MutableList<Chat> {
        return queryFactory
            .select(chat)
            .from(chat)
            .where(
                chat.isDeleted.eq(IsDeleted.FALSE)
                    .and(chat.chatroom.eq(chatroom))
                    .let {
                        // 커서가 있는 경우, createdAt이 커서보다 작은 항목만 가져옴
                        cursor?.let { cur -> it.and(chat.createdAt.lt(cur)) } ?: it
                    }
            )
            .orderBy(chat.createdAt.desc())
            .limit(limit.toLong()) // 페이지 사이즈
            .fetch()
    }
}
