package com.grepp.nbe1_3_team04.chat.service.response

import com.grepp.nbe1_3_team04.chat.domain.ChatMember
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage


data class ChatMemberResponse(
    val chatroomId: Long,
    val memberId: Long
) {
    constructor(chatMember: ChatMember) : this(
        requireNotNull(chatMember.chatroom.chatroomId) {ExceptionMessage.REQUIRE_NOT_NULL_ID.text} ,
        requireNotNull(chatMember.member.memberId) {ExceptionMessage.REQUIRE_NOT_NULL_ID.text}
    )
}
