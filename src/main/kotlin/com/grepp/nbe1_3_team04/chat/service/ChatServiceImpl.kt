package com.grepp.nbe1_3_team04.chat.service

import com.grepp.nbe1_3_team04.chat.domain.Chat
import com.grepp.nbe1_3_team04.chat.domain.Chatroom
import com.grepp.nbe1_3_team04.chat.repository.ChatJDBCRepository
import com.grepp.nbe1_3_team04.chat.repository.ChatMemberRepository
import com.grepp.nbe1_3_team04.chat.repository.ChatRepository
import com.grepp.nbe1_3_team04.chat.repository.ChatroomRepository
import com.grepp.nbe1_3_team04.chat.service.request.ChatServiceRequest
import com.grepp.nbe1_3_team04.chat.service.request.ChatUpdateServiceRequest
import com.grepp.nbe1_3_team04.chat.service.response.ChatResponse
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.jwt.JwtTokenUtil
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime


@Service
class ChatServiceImpl(
    private val chatRepository: ChatRepository,
    private val chatroomRepository: ChatroomRepository,
    private val memberRepository: MemberRepository,
    private val chatMemberRepository: ChatMemberRepository,
    private val redisPublisher: RedisPublisher,
    private val jwtTokenUtil: JwtTokenUtil,
    private val redisTemplate: RedisTemplate<String, Chat>,
    private val chatJDBCRepository: ChatJDBCRepository
) : ChatService {

    /**
     * 메세지 보내기
     *
     * @param request
     */
    @Transactional
    override fun sendMessage(request: ChatServiceRequest, token: String) {
        val member = getMemberByToken(token)
        val chatroom = getChatroom(request.chatroomId)

        checkMemberInChatroom(member, chatroom)

        // 메시지 생성
        val chat = Chat.createTalkChat(chatroom, member, request.message)
        chat.updateTimeToNow()

        // redis에 채팅 저장
        saveChatInRedis(chat)

        // Redis에 메시지 발행
        redisPublisher.publish(chat)
    }

    /**
     * 특정 채팅방에 과거 메세지 조회
     * 페이지네이션
     * 채팅방 메세지를 보려면 채팅방에 소속된 멤버여야 함
     */
    @Transactional(readOnly = true)
    override fun getChatList(chatroomId: Long, pageRequest: PageRequest, member: Member, cursor: LocalDateTime?): List<ChatResponse> {
        val chatroom = getChatroom(chatroomId)
        checkMemberInChatroom(member, chatroom)

        return toResponseList(getChatMessages(chatroom, cursor, pageRequest))
    }

    /**
     * 채팅 수정
     */
    @Transactional
    override fun updateChat(request: ChatUpdateServiceRequest, member: Member, chatId: Long): ChatResponse {
        val chat = getChat(chatId)

        checkChatByMember(member, chat)

        chat.updateMessage(request.message)

        return ChatResponse(chat)
    }

    /**
     * 채팅 삭제
     */
    @Transactional
    override fun deleteChat(member: Member, chatId: Long): Long {
        val chat = getChat(chatId)

        checkChatByMember(member, chat)

        chatRepository.delete(chat)

        return chatId
    }

    /**
     * 채팅방에 소속된 멤버인지 검증하는 메소드
     */
    private fun checkMemberInChatroom(member: Member, chatroom: Chatroom) {
        require(
            chatMemberRepository.existByMemberAndChatroom(
                member,
                chatroom
            )
        ) { ExceptionMessage.MEMBER_NOT_IN_CHATROOM.text }
    }

    /**
     * 채팅을 작성한 멤버인지 검증하는 메소드
     *
     * @param member
     * @param chat
     */
    private fun checkChatByMember(member: Member, chat: Chat) {
        require(chat.member == member) { ExceptionMessage.UNAUTHORIZED_MESSAGE_EDIT.text }
    }

    private fun getMemberByToken(token: String): Member {
        // 채팅방에 참여한 멤버인지 검증
        val accessToken = token.substring(7).trim { it <= ' ' }
        val email = jwtTokenUtil.getEmailFromToken(accessToken)
        return memberRepository.findByEmail(email) ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_NOT_FOUND.text)
    }

    private fun getChatroom(chatroomId: Long): Chatroom {
        return chatroomRepository.findByChatroomId(chatroomId) ?: throw IllegalArgumentException(ExceptionMessage.CHATROOM_NOT_FOUND.text)
    }

    private fun getChat(chatId: Long): Chat {
        return chatRepository.findByChatId(chatId) ?: throw IllegalArgumentException(ExceptionMessage.CHAT_NOT_FOUND.text)
    }

    //////////////////// 채팅 조회 및 저장 //////////////////////////

    // 채팅을 redis에 저장
    private fun saveChatInRedis(chat: Chat) {
        val key = "chatroom:${chat.chatroom.chatroomId}:new"
        val score = localDateTimeToDouble(chat.createdAt)
        redisTemplate.opsForZSet().add(key, chat, score)
    }

    // 채팅을 redis에서 조회
    private fun getChatMessagesWithCursor(chatroom: Chatroom, cursor: LocalDateTime?, pageable: Pageable): List<Chat> {
        val regularKey = "chatroom:${chatroom.chatroomId}"
        val newKey = "chatroom:${chatroom.chatroomId}:new"
        val score: Double? = cursor?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()?.toDouble()
        val endScore = score?.let { it - 0.1 } ?: System.currentTimeMillis().toDouble() // 커서 시간을 포함하지 않게 // 기본 커서는 현재 시간
        val startScore = Double.NEGATIVE_INFINITY

        val regularMessages = redisTemplate.opsForZSet() // 기존 메세지 조회
            .reverseRangeByScore(regularKey, startScore, endScore, 0, pageable.pageSize.toLong()) // 커서 이전의 메시지 조회
            ?.filterIsInstance<Chat>() ?: emptyList()

        val newMessages = redisTemplate.opsForZSet() // 최신 메세지 조회
            .reverseRangeByScore(newKey, startScore, endScore, 0, pageable.pageSize.toLong()) // 커서 이전의 메시지 조회
            ?.filterIsInstance<Chat>() ?: emptyList()

        return regularMessages + newMessages
    }

    // 채팅을 RDBMS에서 조회
    private fun getChatMessagesWithCursorFromDb(chatroom: Chatroom, cursor: LocalDateTime?, limit: Int): List<Chat> {
        val messagesFromDb = chatRepository.findChatByChatroomList(chatroom, limit, cursor)

        messagesFromDb.forEach { message ->
            val score = localDateTimeToDouble(message.createdAt)
            redisTemplate.opsForZSet().add("chatroom:${chatroom.chatroomId}", message, score)
        }

        return messagesFromDb
    }

    // redis에서 조회한 채팅이 있는지 체크 후 없다면 db에서 조회
    private fun getChatMessages(chatroom: Chatroom, cursor: LocalDateTime?, pageable: Pageable): List<Chat> {
        val messages = getChatMessagesWithCursor(chatroom, cursor, pageable)

        return if (messages.isEmpty()){
            getChatMessagesWithCursorFromDb(chatroom, cursor, pageable.pageSize)
        }
        else if(messages.size<pageable.pageSize){
            messages + getChatMessagesWithCursorFromDb(chatroom, messages.last().createdAt?:cursor, pageable.pageSize-messages.size)
        }
        else messages
    }

    // response List로 만들어주기
    private fun toResponseList(chats: List<Chat>): List<ChatResponse> {
        return chats.map { ChatResponse(it) }
    }

    // localDateTime을 Redis에서 사용할 수 있게 double로 변경
    private fun localDateTimeToDouble(cursor: LocalDateTime?): Double {
        return cursor?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()?.toDouble()
            ?: throw IllegalArgumentException(ExceptionMessage.REQUIRE_NOT_NULL_CREATED_AT.text)
    }

    ////////////////////// 새로운 채팅 batch Insert /////////////////////////

    @Transactional
    @Scheduled(cron = "0 30 * * * *") // 1시간마다 실행
    fun batchInsertChatMessages() {
        val currentTimestamp = Instant.now().toEpochMilli().toDouble()
        val chatRoomIds = chatroomRepository.findAll().map { it.chatroomId }
        val chatRoomKeys = redisTemplate.keys("chatroom:*:new")

        chatRoomIds.forEach { id ->
            redisTemplate.execute(RedisCallback {
                it.multi() // 트랜잭션 시작
                val key = "chatroom:$id:new"

                // redis 에서 특정 채팅방의 모든 메세지를 가져옴
                val messages = redisTemplate.opsForZSet()
                    .rangeByScore(key, Double.NEGATIVE_INFINITY, currentTimestamp)
                    ?.filterIsInstance<Chat>()
                    ?.filter { chat -> chat.chatId == null } ?: emptyList() // chatId가 null인 경우만 필터링


                if (messages.isNotEmpty()) {
                    // DB에 저장 (저장 시 자동으로 chatId가 생성됨)
                    chatJDBCRepository.batchInsert(messages)
                    // Redis에서 저장 새 메세지 키 이름 변경
                    moveNewMessagesKey(id)
                }
                it.exec() // 트랜잭션 종료
            })
        }
    }

    // redis 키 이름 변경
    private fun moveNewMessagesKey(chatroomId: Long?) {
        val newKey = "chatroom:$chatroomId:new"
        val existingKey = "chatroom:$chatroomId"

        if (redisTemplate.hasKey(newKey)) {
            // new 키가 존재하는 경우 기존 키로 이름 변경
            redisTemplate.rename(newKey, existingKey)
        }
    }

    ////////////////// 레디스 캐시 동기화 /////////////////////////

    // 레디스 캐시를 최근 1주일 채팅으로 동기화
    // 매주 일요일 새벽 4시
    @Transactional
    @Scheduled(cron = "0 0 4 * * SUN")
    fun syncChatWithCache() {
        val oneWeekAgo = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDateTime().minusWeeks(1)
        val chatRoomIds = chatroomRepository.findAll().map { it.chatroomId }

        chatRoomIds.forEach { chatRoomId ->
            // 오래된 캐시 메시지를 삭제
            deleteOldCacheMessages(chatRoomId, oneWeekAgo)
        }

        val messages = getMessagesForLastWeek(oneWeekAgo)

        messages.forEach { message ->
            val redisKey = "chatroom:${message.chatroom.chatroomId}"

            // Redis에 이미 존재하는지 확인
            val existingMessages = redisTemplate.opsForZSet()
                .range(redisKey, 0, -1)
                ?.map { it as Chat } ?: emptyList()

            // 중복되지 않는 경우 Redis에 추가
            if (existingMessages.none { it.chatId == message.chatId }) {
                val score = localDateTimeToDouble(message.createdAt)
                redisTemplate.opsForZSet().add(redisKey, message, score)
            }
        }
    }

    // 최근 일주일보다 오래된 채팅 캐시 삭제
    private fun deleteOldCacheMessages(chatroomId: Long?, oneWeekAgo: LocalDateTime) {
        val redisKey = "chatroom:$chatroomId"

        // oneWeekAgo를 epoch milli로 변환
        val scoreThreshold = oneWeekAgo.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toDouble()

        // scoreThreshold 이하의 메시지를 삭제합니다.
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, Double.NEGATIVE_INFINITY, scoreThreshold)
    }

    // 최근 1주간 채팅 가져오기
    private fun getMessagesForLastWeek(oneWeekAgo: LocalDateTime): List<Chat> {
        return chatRepository.findAllByCreatedAtAfter(oneWeekAgo)
    }
}
