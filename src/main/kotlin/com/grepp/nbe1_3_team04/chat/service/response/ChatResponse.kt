package com.grepp.nbe1_3_team04.chat.service.response

import com.grepp.nbe1_3_team04.chat.domain.Chat
import com.grepp.nbe1_3_team04.chat.domain.ChatType
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import java.time.LocalDateTime

data class ChatResponse(
    val chatId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val chatroomResponse: ChatroomResponse,
    val memberInfo: ChatMemberInfo,
    val chatType: ChatType,
    val text: String
) {
    constructor(chat: Chat) : this(
        requireNotNull(chat.chatId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
        requireNotNull(chat.createdAt) {ExceptionMessage.REQUIRE_NOT_NULL_CREATED_AT.text},
        requireNotNull(chat.updatedAt) {ExceptionMessage.REQUIRE_NOT_NULL_UPDATED_AT.text},
        ChatroomResponse(chat.chatroom),
        ChatMemberInfo(chat.member),
        chat.chatType,
        chat.text
    )
}
