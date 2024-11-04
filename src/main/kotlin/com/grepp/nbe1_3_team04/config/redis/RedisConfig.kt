package com.grepp.nbe1_3_team04.config.redis

import com.grepp.nbe1_3_team04.chat.domain.Chat
import com.grepp.nbe1_3_team04.chat.service.RedisSubscriber
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableRedisRepositories
@EnableTransactionManagement
@EnableCaching
class RedisConfig {
    //redis pub/sub 사용할 때의 chatting Topic
    @Bean
    fun channelTopic(): ChannelTopic {
        return ChannelTopic(CHAT_ROOMS)
    }

    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory?,
        listenerAdapter: MessageListenerAdapter?,
        channelTopic: ChannelTopic?
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory!!)
        container.addMessageListener(listenerAdapter!!, channelTopic!!)
        return container
    }


    @Bean
    fun listenerAdapter(subscriber: RedisSubscriber?): MessageListenerAdapter {
        return MessageListenerAdapter(subscriber!!, "sendMessage")
    }

    @Primary
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = connectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(String::class.java)
        redisTemplate.setEnableTransactionSupport(true)
        return redisTemplate
    }

    @Bean
    fun chatRedisTemplate(connectionFactory: RedisConnectionFactory?): RedisTemplate<String, Chat> {
        val redisTemplate = RedisTemplate<String, Chat>()
        redisTemplate.connectionFactory = connectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(Chat::class.java) // Chat에 맞춘 직렬화
        redisTemplate.setEnableTransactionSupport(true)
        return redisTemplate
    }

    @Bean
    fun transactionManager(): PlatformTransactionManager {
        return JpaTransactionManager()
    }

    companion object {
        private const val CHAT_ROOMS = "CHAT_ROOM"
    }
}
