package com.grepp.nbe1_3_team04.chat.service

import com.grepp.nbe1_3_team04.chat.domain.Chat
import com.grepp.nbe1_3_team04.chat.domain.ChatMember
import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.chat.repository.ChatMemberRepository
import com.grepp.nbe1_3_team04.chat.repository.ChatRepository
import com.grepp.nbe1_3_team04.chat.repository.ChatroomRepository
import com.grepp.nbe1_3_team04.chat.service.request.ChatMemberServiceRequest
import com.grepp.nbe1_3_team04.chat.service.response.ChatMemberResponse
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import com.grepp.nbe1_3_team04.reservation.domain.Participant
import com.grepp.nbe1_3_team04.team.domain.TeamMember
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class ChatMemberServiceImpl(
    private val chatMemberRepository: ChatMemberRepository,
    private val memberRepository: MemberRepository,
    private val chatroomRepository: ChatroomRepository,
    private val chatRepository: ChatRepository,
    private val redisPublisher: RedisPublisher
) : ChatMemberService {

    /**
     * 개인 채팅방 초대
     *
     * @param request
     * @return
     */
    @Transactional
    override fun joinChatMember(request: ChatMemberServiceRequest): ChatMemberResponse {
        val member = getMember(request.memberId)
        val chatroom = getChatroomByChatroomId(request.chatroomId)

        return addChatMember(member, chatroom)
    }

    @Transactional
    override fun joinTeamChatMember(member: Member, teamId: Long) {
        val chatroom = getChatroomByTeamId(teamId)

        addChatMember(member, chatroom)
    }

    @Transactional
    override fun joinReservationChatMember(member: Member, reservationId: Long) {
        val chatroom = getChatroomByReservationId(reservationId)

        addChatMember(member, chatroom)
    }

    private fun addChatMember(member: Member, chatroom: Chatroom): ChatMemberResponse {
        checkMemberNotInChatroom(member, chatroom)

        val chatMember: ChatMember = chatMemberRepository.save(ChatMember.create(member, chatroom))

        val chat = Chat.createEnterChat(chatroom, member)
        chatRepository.save(chat)

        redisPublisher.publish(chat)

        return ChatMemberResponse(chatMember)
    }

    /**
     * 팀원 채팅방 초대
     *
     * @param teamMembers
     */
    @Transactional
    override fun joinChatTeam(teamMembers: List<TeamMember>, teamId: Long) {
        val chatroom = getChatroomByTeamId(teamId)
        val members = teamMembers.map { it.member }

        joinChatMembers(members, chatroom)
    }

    /**
     * 게임 참여 인원 채팅방 초대
     *
     * @param gameMembers
     */
    @Transactional
    override fun joinChatGame(gameMembers: List<Participant>, reservationId: Long) {
        val chatroom = getChatroomByReservationId(reservationId)

        val members = gameMembers.map { it.member }

        joinChatMembers(members, chatroom)
    }

    /**
     * 단체로 채팅방 초대
     *
     * @param members
     */
    @Transactional
    override fun joinChatMembers(members: List<Member>, chatroom: Chatroom) {
        val oldMembersId = chatMemberRepository.findByChatroom(chatroom)
            .map { it.member.memberId }

        val chatMembers: MutableList<ChatMember> = ArrayList()

        for (member in members) {
            if (oldMembersId.contains(member.memberId)) {
                continue
            }
            chatMembers.add(ChatMember.create(member, chatroom))
        }

        chatMemberRepository.saveAll(chatMembers)

        val chat = Chat.createGroupEnterChat(chatroom, chatMembers)
        chatRepository.save(chat)

        redisPublisher.publish(chat)
    }

    /**
     * 개인 인원 채팅방 나가기
     *
     * @param request
     * @return
     */
    @Transactional
    override fun leaveChatMember(request: ChatMemberServiceRequest): ChatMemberResponse {
        val member = getMember(request.memberId)
        val chatroom = getChatroomByChatroomId(request.chatroomId)

        return removeChatMember(member, chatroom)
    }

    @Transactional
    override fun leaveTeamChatMember(member: Member, teamId: Long) {
        val chatroom = getChatroomByTeamId(teamId)

        removeChatMember(member, chatroom)
    }

    @Transactional
    override fun leaveReservationChatMember(member: Member, reservationId: Long) {
        val chatroom = getChatroomByReservationId(reservationId)

        removeChatMember(member, chatroom)
    }

    private fun removeChatMember(member: Member, chatroom: Chatroom): ChatMemberResponse {
        checkMemberInChatroom(member, chatroom)

        chatMemberRepository.deleteByMemberAndChatroom(member, chatroom)

        val chat = Chat.createQuitChat(chatroom, member)
        chatRepository.save(chat)

        redisPublisher.publish(chat)

        return ChatMemberResponse(chatroom.chatroomId!!, member.memberId!!)
    }

    /**
     * 채팅방이 삭제됐을 때 채팅방에 참가한 사람들 연관관계 삭제
     *
     * @param chatroomId 채팅방 ID
     * @return
     */
    @Transactional
    override fun leaveChatRoom(chatroomId: Long) {
        val chatroom = getChatroomByChatroomId(chatroomId)

        chatMemberRepository.updateIsDeletedForChatroom(chatroom)
    }

    @Transactional
    override fun leaveTeamChatRoom(teamId: Long) {
        val chatroom = getChatroomByTeamId(teamId)

        chatMemberRepository.updateIsDeletedForChatroom(chatroom)
    }

    @Transactional
    override fun leaveReservationChatRoom(reservationId: Long) {
        val chatroom = getChatroomByReservationId(reservationId)

        chatMemberRepository.updateIsDeletedForChatroom(chatroom)
    }

    private fun checkMemberInChatroom(member: Member, chatroom: Chatroom) {
        require(
            chatMemberRepository.existByMemberAndChatroom(
                member,
                chatroom
            )
        ) { ExceptionMessage.MEMBER_NOT_IN_CHATROOM.text }
    }

    private fun checkMemberNotInChatroom(member: Member, chatroom: Chatroom) {
        require(
            !chatMemberRepository.existByMemberAndChatroom(
                member,
                chatroom
            )
        ) { ExceptionMessage.MEMBER_IN_CHATROOM.text }
    }

    private fun getMember(memberId: Long): Member {
        return memberRepository.findByMemberId(memberId) ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_NOT_FOUND.text)
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
