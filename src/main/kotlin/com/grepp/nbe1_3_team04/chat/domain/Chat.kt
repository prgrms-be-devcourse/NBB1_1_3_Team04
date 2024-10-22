package com.grepp.nbe1_3_team04.chat.domain

import com.grepp.nbe1_3_team04.global.domain.BaseEntity
import com.grepp.nbe1_3_team04.member.domain.Member
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.SQLDelete
import java.io.Serial
import java.io.Serializable

@Entity
@SQLDelete(sql = "UPDATE chat SET is_deleted = 'true' WHERE chat_id = ?")
class Chat(
    chatroom: Chatroom,
    member: Member,
    chatType: ChatType,
    text: String
) : BaseEntity(), Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var chatId: Long? = null
        protected set

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    var chatRoom: Chatroom = chatroom
        protected set

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member = member
        protected set

    var text: @NotNull String = text
        protected set

    @Enumerated(EnumType.STRING)
    var chatType: @NotNull ChatType = chatType
        protected set

    fun updateMessage(message: String) {
        this.text = message
    }

    companion object {
        @Serial
        private val serialVersionUID = -2501100146410218751L

        fun create(chatroom: Chatroom, member: Member, chatType: ChatType, text: String): Chat {
            return Chat(chatroom, member, chatType, text)
        }

        fun createTalkChat(chatroom: Chatroom, member: Member, text: String): Chat {
            return Chat(chatroom, member, ChatType.TALK, text)
        }

        fun createEnterChat(chatroom: Chatroom, member: Member): Chat {
            return Chat(chatroom, member, ChatType.ENTER, member.name + "님이 입장했습니다.")
        }

        fun createGroupEnterChat(chatRoom: Chatroom, chatMembers: MutableList<ChatMember>): Chat {
            return Chat(
                chatRoom,
                chatMembers[0].member,
                ChatType.ENTER,
                chatMembers[0].member.name + "님 등 " + chatMembers.size + "명이 입장했습니다."
            )
        }

        fun createQuitChat(chatroom: Chatroom, member: Member): Chat {
            return Chat(chatroom, member, ChatType.QUIT, member.name + "님이 채팅방을 떠났습니다.")
        }
    }
}
