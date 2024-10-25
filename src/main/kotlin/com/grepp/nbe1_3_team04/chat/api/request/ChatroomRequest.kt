package com.grepp.nbe1_3_team04.chat.api.request

import com.grepp.nbe1_3_team04.chat.service.request.ChatroomServiceRequest
import jakarta.validation.constraints.NotBlank

data class ChatroomRequest(
    @field:NotBlank(message = "채팅방 이름은 필수입니다.")
    val name: String?
) {
    fun toServiceRequest(): ChatroomServiceRequest {
        return ChatroomServiceRequest(name!!)
    }
}
