package com.grepp.nbe1_3_team04.chat.api

import com.grepp.nbe1_3_team04.chat.api.request.ChatroomRequest
import com.grepp.nbe1_3_team04.chat.service.ChatroomService
import com.grepp.nbe1_3_team04.chat.service.response.ChatroomResponse
import com.grepp.nbe1_3_team04.global.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails


@RestController
@RequestMapping("/api/v1/chat/room")
class ChatroomApi(
    private val chatroomService: ChatroomService
) {

    /**
     * 채팅방 생성
     * 팀, 예약 생성시 같이 실행해주기
     */
    @PostMapping
    fun createChatroom(@RequestBody chatroomRequest: @Valid ChatroomRequest): ApiResponse<ChatroomResponse> {
        return ApiResponse.created(chatroomService.createChatroom(chatroomRequest.toServiceRequest()))
    }

    /**
     * 채팅방 삭제
     */
    @DeleteMapping("/{chatroomId}")
    fun deleteChatroom(@PathVariable("chatroomId") chatroomId: Long): ApiResponse<Long> {
        return ApiResponse.ok(chatroomService.deleteChatroomByChatroomId(chatroomId))
    }

    /**
     * 채팅방 수정
     */
    @PutMapping("/{chatroomId}")
    fun updateChatroom(
        @PathVariable("chatroomId") chatroomId: Long,
        @RequestBody chatroomRequest: @Valid ChatroomRequest
    ): ApiResponse<ChatroomResponse> {
        return ApiResponse.ok(chatroomService.updateChatroom(chatroomId, chatroomRequest.toServiceRequest()))
    }

    /**
     * 채팅방 조회
     */
    @GetMapping
    fun getMyChatroomList(@AuthenticationPrincipal principalDetails: PrincipalDetails): ApiResponse<List<ChatroomResponse>> {
        return ApiResponse.ok(chatroomService.getMyChatroom(principalDetails.member))
    }
}
