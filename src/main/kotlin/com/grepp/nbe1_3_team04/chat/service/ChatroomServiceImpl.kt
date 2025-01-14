package com.grepp.nbe1_3_team04.chat.service

import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.chat.domain.ReservationChatroom
import com.grepp.nbe1_3_team04.chat.domain.TeamChatroom
import com.grepp.nbe1_3_team04.chat.repository.ChatroomRepository
import com.grepp.nbe1_3_team04.chat.repository.RedisChatroomRepository
import com.grepp.nbe1_3_team04.chat.service.request.ChatroomServiceRequest
import com.grepp.nbe1_3_team04.chat.service.response.ChatroomResponse
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.member.domain.Member
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class ChatroomServiceImpl(
    private val chatroomRepository: ChatroomRepository,
    private val redisChatroomRepository: RedisChatroomRepository
) : ChatroomService {

    /**
     * 채팅방 생성
     * 채팅방을 만든 사람은 자동으로 채팅방 초대
     *
     * @param request 채팅방 이름 ( 팀 or 예약 만들어질 때 이름 넣어서 보내주기 )
     * @return
     */
    @Transactional
    override fun createChatroom(request: ChatroomServiceRequest): ChatroomResponse {
        val chatroom: Chatroom = chatroomRepository.save(Chatroom.create(request.name))

        // redis Hash에 저장
        redisChatroomRepository.createChatRoom(chatroom)

        return ChatroomResponse(chatroom)
    }

    @Transactional
    override fun createTeamChatroom(request: ChatroomServiceRequest, teamId: Long): ChatroomResponse {
        val chatroom: Chatroom = chatroomRepository.save(TeamChatroom.create(request.name, teamId))

        // redis Hash에 저장
        redisChatroomRepository.createChatRoom(chatroom)

        return ChatroomResponse(chatroom)
    }

    @Transactional
    override fun createReservationChatroom(request: ChatroomServiceRequest, reservationId: Long): ChatroomResponse {
        val chatroom: Chatroom = chatroomRepository.save(ReservationChatroom.create(request.name, reservationId))

        // redis Hash에 저장
        redisChatroomRepository.createChatRoom(chatroom)

        return ChatroomResponse(chatroom)
    }

    /**
     * 채팅방 삭제 ( 채팅방 인원 삭제도 같이 진행해야 함 )
     *
     * @param chatroomId
     * @return
     */
    @Transactional
    override fun deleteChatroomByChatroomId(chatroomId: Long): Long {
        val chatroom = getChatroomByChatroomId(chatroomId)

        return deleteChatroom(chatroom)
    }

    @Transactional
    override fun deleteTeamChatroom(teamId: Long): Long {
        val chatroom = getChatroomByTeamId(teamId)

        return deleteChatroom(chatroom)
    }

    @Transactional
    override fun deleteReservationChatroom(reservationId: Long): Long {
        val chatroom = getChatroomByReservationId(reservationId)

        return deleteChatroom(chatroom)
    }

    /**
     * 채팅방 수정
     */
    @Transactional
    override fun updateChatroom(chatroomId: Long, request: ChatroomServiceRequest): ChatroomResponse {
        val chatroom = getChatroomByChatroomId(chatroomId)

        chatroom.updateName(request.name)

        return ChatroomResponse(chatroom)
    }

    /**
     * 내가 참여한 채팅방 조회
     */
    @Transactional(readOnly = true)
    override fun getMyChatroom(member: Member): List<ChatroomResponse> {
        return chatroomRepository.findChatroomByMember(member)
            .map(::ChatroomResponse)
    }

    private fun deleteChatroom(chatroom: Chatroom): Long {
        redisChatroomRepository.deleteChatroomFromRedis(chatroom.chatroomId!!)

        chatroomRepository.deleteById(chatroom.chatroomId!!)
        return chatroom.chatroomId!!
    }

    private fun getChatroomByChatroomId(chatroomId: Long): Chatroom {
        return chatroomRepository.findByChatroomId(chatroomId) ?: throw IllegalArgumentException(ExceptionMessage.CHATROOM_NOT_FOUND.text)
    }

    private fun getChatroomByTeamId(teamId: Long): Chatroom {
        return chatroomRepository.findByTeamId(teamId) ?: throw IllegalArgumentException(ExceptionMessage.CHATROOM_NOT_FOUND.text)
    }

    private fun getChatroomByReservationId(reservationId: Long): Chatroom {
        return chatroomRepository.findByReservationId(reservationId) ?: throw IllegalArgumentException(ExceptionMessage.CHATROOM_NOT_FOUND.text)
    }
}
