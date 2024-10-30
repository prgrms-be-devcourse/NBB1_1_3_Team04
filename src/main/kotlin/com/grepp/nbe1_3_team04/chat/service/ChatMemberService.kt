package com.grepp.nbe1_3_team04.chat.service

import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.chat.service.request.ChatMemberServiceRequest
import com.grepp.nbe1_3_team04.chat.service.response.ChatMemberResponse
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.reservation.domain.Participant
import com.grepp.nbe1_3_team04.team.domain.TeamMember
import org.springframework.stereotype.Service

@Service
interface ChatMemberService {
    fun joinChatMember(request: ChatMemberServiceRequest): ChatMemberResponse

    fun joinChatTeam(teamMembers: List<TeamMember>, teamId: Long)

    fun joinChatGame(gameMembers: List<Participant>, reservationId: Long)

    fun joinChatMembers(members: List<Member>, chatroom: Chatroom)

    fun leaveChatMember(request: ChatMemberServiceRequest): ChatMemberResponse

    fun leaveChatRoom(chatroomId: Long)

    fun joinTeamChatMember(member: Member, teamId: Long)

    fun joinReservationChatMember(member: Member, reservationId: Long)

    fun leaveTeamChatRoom(teamId: Long)

    fun leaveReservationChatRoom(reservationId: Long)

    fun leaveTeamChatMember(member: Member, teamId: Long)

    fun leaveReservationChatMember(member: Member, reservationId: Long)
}
