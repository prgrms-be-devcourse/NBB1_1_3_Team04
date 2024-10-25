package com.grepp.nbe1_3_team04.chat.api.request

import com.grepp.nbe1_3_team04.chat.service.request.ChatUpdateServiceRequest
import jakarta.validation.constraints.NotBlank

data class ChatUpdateRequest(
    @field:NotBlank(message = "채팅 메세지는 필수입니다.")
    val message: String?
) {
    fun toServiceRequest(): ChatUpdateServiceRequest {
        return ChatUpdateServiceRequest(message!!)
    }
}
