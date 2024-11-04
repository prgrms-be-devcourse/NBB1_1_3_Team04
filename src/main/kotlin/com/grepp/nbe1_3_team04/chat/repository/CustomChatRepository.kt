package com.grepp.nbe1_3_team04.chat.repository

import com.grepp.nbe1_3_team04.chat.domain.Chat
import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.chat.service.response.ChatResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.time.LocalDateTime

interface CustomChatRepository {
    fun findChatByChatroomPage(chatroom: Chatroom, pageable: Pageable, cursor: LocalDateTime?): Slice<ChatResponse>

    fun findChatByChatroomList(chatroom: Chatroom, limit: Int, cursor: LocalDateTime?): MutableList<Chat>
}
