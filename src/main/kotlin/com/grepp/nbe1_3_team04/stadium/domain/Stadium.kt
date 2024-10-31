package com.grepp.nbe1_3_team04.stadium.domain

import com.grepp.nbe1_3_team04.global.domain.BaseEntity
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.member.domain.Member
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.SQLDelete
import org.locationtech.jts.geom.Point

@SQLDelete(sql = "UPDATE stadium SET is_deleted = 'TRUE' WHERE stadium_id = ?")
@Entity
class Stadium private constructor(
    member: Member,
    name: String,
    address: String,
    phoneNumber: String,
    description: String?,
    location: Point
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val stadiumId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member = member
        protected set

    @field:NotNull
    @Column(nullable = false)
    var name: String = name
        protected set

    @field:NotNull
    @Column(nullable = false)
    var address: String = address
        protected set

    @field:NotNull
    @Column(nullable = false)
    var phoneNumber: String = phoneNumber
        protected set

    @Column(length = 200, nullable = true)
    var description: String? = description
        protected set

    @field:NotNull
    @Column(nullable = false, columnDefinition = "POINT")
    var location: Point = location
        protected set

    fun updateStadium(
        memberId: Long,
        name: String,
        address: String,
        phoneNumber: String,
        description: String?,
        location: Point
    ) {
        checkMember(memberId)
        this.name = name
        this.address = address
        this.phoneNumber = phoneNumber
        this.description = description
        this.location = location
    }

    fun deleteStadium(memberId: Long) {
        checkMember(memberId)
    }

    fun createCourt(memberId: Long) {
        checkMember(memberId)
    }

    private fun checkMember(memberId: Long) {
        if (this.member.memberId != memberId) {
            throw IllegalArgumentException(ExceptionMessage.STADIUM_NOT_OWNED_BY_MEMBER.text)
        }
    }

    companion object {
        fun create(
            member: Member,
            name: String,
            address: String,
            phoneNumber: String,
            description: String?,
            location: Point
        ): Stadium {
            return Stadium(member, name, address, phoneNumber, description, location)
        }
    }
}