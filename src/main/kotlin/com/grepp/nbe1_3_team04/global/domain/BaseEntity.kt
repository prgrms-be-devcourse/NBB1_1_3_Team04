package com.grepp.nbe1_3_team04.global.domain

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class) // 스프링 데이터 JPA의 감사 기능 -> 엔티티가 생성/수정 될때 자동으로 설정
abstract class BaseEntity {

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null
        protected set

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
        protected set

    @Enumerated(EnumType.STRING)
    var isDeleted = IsDeleted.FALSE

    fun updateTimeToNow(){
        createdAt = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime()
        updatedAt = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDateTime()
    }

}
