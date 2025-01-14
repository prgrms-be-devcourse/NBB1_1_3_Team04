package com.grepp.nbe1_3_team04.config.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Profile("dev | test")
@Configuration
class ExternalRedisConfig(
    @Value("\${spring.data.redis.host}")
    private val redisHostName: String,
    @Value("\${spring.data.redis.port}")
    private val redisPort: Int
) {

    @Profile("dev | test")
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = redisHostName
        redisStandaloneConfiguration.port = redisPort

        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }
}
