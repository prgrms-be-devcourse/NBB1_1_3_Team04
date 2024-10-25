package com.grepp.nbe1_3_team04.chat.api.request

import com.grepp.nbe1_3_team04.chat.service.request.ChatServiceRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ChatRequest(
    @field:NotNull(message = "채팅방 아이디는 필수입니다.")
    val chatroomId: Long?,

    @field:NotBlank(message = "채팅 메세지는 필수입니다.")
    val message: String?
) {
    fun toServiceRequest(): ChatServiceRequest {
        return ChatServiceRequest(chatroomId!!, message!!)
    }
}
