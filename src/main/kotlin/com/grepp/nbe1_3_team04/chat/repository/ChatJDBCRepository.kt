package com.grepp.nbe1_3_team04.chat.repository

import com.grepp.nbe1_3_team04.chat.domain.Chat
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.PreparedStatement
import java.sql.Timestamp

@Repository
class ChatJDBCRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    @Transactional
    fun batchInsert(list: List<Chat>){
        val sql = """
            INSERT INTO chat (chat_type, text, chatroom_id, member_id, created_at, is_deleted, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        try {
            jdbcTemplate.batchUpdate(
                 sql, object : BatchPreparedStatementSetter {
                override fun setValues(ps: PreparedStatement, i: Int) {
                    ps.setString(1, list[i].chatType.name)
                    ps.setString(2, list[i].text)
                    ps.setLong(3, requireNotNull(list[i].chatroom.chatroomId) { "Chatroom ID cannot be null" })
                    ps.setLong(4, requireNotNull(list[i].member.memberId) { "Member ID cannot be null" })
                    ps.setTimestamp(5, list[i].createdAt?.let { Timestamp.valueOf(it) })
                    ps.setString(6, list[i].isDeleted.name)
                    ps.setTimestamp(7, list[i].updatedAt?.let { Timestamp.valueOf(it) })
                }

                override fun getBatchSize(): Int {
                    return list.size
                }
            })
        } catch (e: Exception) {
            throw IllegalArgumentException(ExceptionMessage.CANNOT_BATCH_INSERT.text)
        }
    }

}