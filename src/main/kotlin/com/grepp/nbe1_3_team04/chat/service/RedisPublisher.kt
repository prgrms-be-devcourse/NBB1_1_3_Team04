package com.grepp.nbe1_3_team04.chat.service

import com.grepp.nbe1_3_team04.chat.domain.Chat
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service

@Service
class RedisPublisher(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val channelTopic: ChannelTopic
) {

    fun publish(chat: Chat) {
        redisTemplate.convertAndSend(channelTopic.topic, chat)
    }
}
