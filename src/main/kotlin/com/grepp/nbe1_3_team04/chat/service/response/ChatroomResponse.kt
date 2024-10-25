package com.grepp.nbe1_3_team04.chat.service.response

import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage


data class ChatroomResponse(
    val chatroomId: Long,
    val name: String
) {
    constructor(chatroom: Chatroom) : this(
        requireNotNull(chatroom.chatroomId) { ExceptionMessage.REQUIRE_NOT_NULL_ID.text},
        chatroom.name
    )
}
