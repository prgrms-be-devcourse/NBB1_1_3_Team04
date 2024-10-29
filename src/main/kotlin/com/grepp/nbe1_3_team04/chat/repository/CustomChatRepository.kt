package com.grepp.nbe1_3_team04.chat.repository

import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.chat.service.response.ChatResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface CustomChatRepository {
    fun findChatByChatroom(chatroom: Chatroom, pageable: Pageable): Slice<ChatResponse>
}
