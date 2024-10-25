package com.grepp.nbe1_3_team04.chat.api.request

import com.grepp.nbe1_3_team04.chat.service.request.ChatMemberServiceRequest
import jakarta.validation.constraints.NotNull

data class ChatMemberRequest(
    @field:NotNull(message = "채팅방 아이디는 필수입니다.")
    val chatroomId: Long?,

    @field:NotNull(message = "회원 아이디는 필수입니다.")
    val memberId: Long?
) {
    fun toServiceRequest(): ChatMemberServiceRequest {
        return ChatMemberServiceRequest(chatroomId!!, memberId!!)
    }
}
