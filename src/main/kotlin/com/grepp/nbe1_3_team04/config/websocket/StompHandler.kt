package com.grepp.nbe1_3_team04.config.websocket

import com.grepp.nbe1_3_team04.member.jwt.JwtTokenUtil
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component

@Component
class StompHandler(private val jwtTokenUtil: JwtTokenUtil) : ChannelInterceptor {

    // websocket을 통해 들어온 요청이 처리 되기 전 실행
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = StompHeaderAccessor.wrap(message)
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.command) {
            var token = accessor.getFirstNativeHeader("Authorization")
                ?: throw IllegalArgumentException("Authorization header missing")
            token = token.substring(7).trim { it <= ' ' }
            jwtTokenUtil!!.tokenValidation(token)
        }
        return message
    }
}
