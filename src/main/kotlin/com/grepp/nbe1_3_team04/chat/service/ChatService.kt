package com.grepp.nbe1_3_team04.chat.service

import com.grepp.nbe1_3_team04.chat.service.request.ChatServiceRequest
import com.grepp.nbe1_3_team04.chat.service.request.ChatUpdateServiceRequest
import com.grepp.nbe1_3_team04.chat.service.response.ChatResponse
import com.grepp.nbe1_3_team04.member.domain.Member
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import java.time.LocalDateTime

interface ChatService {
    fun sendMessage(request: ChatServiceRequest, token: String)

    fun getChatList(chatroomId: Long, pageRequest: PageRequest, member: Member, cursor: LocalDateTime?): Slice<ChatResponse>

    fun updateChat(request: ChatUpdateServiceRequest, member: Member, chatId: Long): ChatResponse

    fun deleteChat(member: Member, chatId: Long): Long
}
