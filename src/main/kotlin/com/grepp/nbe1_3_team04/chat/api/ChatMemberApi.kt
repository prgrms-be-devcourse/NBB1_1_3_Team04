package com.grepp.nbe1_3_team04.chat.api

import com.grepp.nbe1_3_team04.chat.api.request.ChatMemberRequest
import com.grepp.nbe1_3_team04.chat.service.ChatMemberService
import com.grepp.nbe1_3_team04.chat.service.response.ChatMemberResponse
import com.grepp.nbe1_3_team04.global.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/chat/member")
class ChatMemberApi(
    private val chatMemberService: ChatMemberService
) {

    /**
     * 채팅방 초대
     */
    @PostMapping
    fun inviteChatMember(@RequestBody @Valid chatMemberRequest: ChatMemberRequest): ApiResponse<ChatMemberResponse> {
        return ApiResponse.created(chatMemberService.joinChatMember(chatMemberRequest.toServiceRequest()))
    }

    /**
     * 채팅방 나감
     */
    @DeleteMapping
    fun removeChatMember(@RequestBody @Valid chatMemberRequest: ChatMemberRequest): ApiResponse<ChatMemberResponse> {
        return ApiResponse.ok(chatMemberService.leaveChatMember(chatMemberRequest.toServiceRequest()))
    }
}
