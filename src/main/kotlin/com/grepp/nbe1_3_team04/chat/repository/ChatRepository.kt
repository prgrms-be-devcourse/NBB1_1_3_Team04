package com.grepp.nbe1_3_team04.chat.repository

import com.grepp.nbe1_3_team04.chat.domain.Chat
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ChatRepository : JpaRepository<Chat, Long>, CustomChatRepository {
    @Query("select c from Chat c where c.isDeleted = 'false' and c.chatroom.chatroomId = ?1")
    fun findByChatRoom_ChatroomId(chatroomId: Long): List<Chat>

    @Query("select c from Chat c where c.isDeleted = 'false' and c.chatId = ?1")
    fun findByChatId(chatId: Long): Chat?

    @Query("SELECT c FROM Chat c WHERE c.isDeleted = 'false' AND c.createdAt >= :date")
    fun findAllByCreatedAtAfter(@Param("date") date: LocalDateTime): List<Chat>
}
