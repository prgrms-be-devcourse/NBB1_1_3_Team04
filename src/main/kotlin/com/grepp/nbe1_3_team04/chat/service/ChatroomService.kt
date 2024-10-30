package com.grepp.nbe1_3_team04.chat.service

import com.grepp.nbe1_3_team04.chat.service.request.ChatroomServiceRequest
import com.grepp.nbe1_3_team04.chat.service.response.ChatroomResponse

interface ChatroomService {
    fun createChatroom(request: ChatroomServiceRequest): ChatroomResponse

    fun deleteChatroomByChatroomId(chatroomId: Long): Long

    fun updateChatroom(chatroomId: Long, request: ChatroomServiceRequest): ChatroomResponse

    fun createReservationChatroom(request: ChatroomServiceRequest, reservationId: Long): ChatroomResponse

    fun createTeamChatroom(request: ChatroomServiceRequest, teamId: Long): ChatroomResponse

    fun deleteTeamChatroom(teamId: Long): Long

    fun deleteReservationChatroom(reservationId: Long): Long
}
