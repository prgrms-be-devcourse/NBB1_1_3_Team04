package com.grepp.nbe1_3_team04.stadium.domain

import com.grepp.nbe1_3_team04.global.domain.BaseEntity
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import org.hibernate.annotations.SQLDelete;

@SQLDelete(sql = "UPDATE court SET is_deleted = 'TRUE' WHERE court_id = ?")
@Entity
class Court private constructor(
    stadium: Stadium,
    name: String,
    description: String?,
    pricePerHour: BigDecimal
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val courtId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    val stadium: Stadium = stadium

    @field:NotNull
    var name: String = name
        protected set

    @Column(length = 200, nullable = true)
    var description: String? = description
        protected set

    @field:NotNull
    var pricePerHour: BigDecimal = pricePerHour
        protected set

    fun updateCourt(stadiumId: Long, memberId: Long, name: String, description: String?, pricePerHour: BigDecimal) {
        checkMember(memberId)
        checkStadium(stadiumId)
        this.name = name
        this.description = description
        this.pricePerHour = pricePerHour
    }

    fun deleteCourt(stadiumId: Long, memberId: Long) {
        checkMember(memberId)
        checkStadium(stadiumId)
    }

    private fun checkStadium(stadiumId: Long) {
        if (this.stadium.stadiumId != stadiumId) {
            throw IllegalArgumentException(ExceptionMessage.COURT_NOT_OWNED_BY_STADIUM.text)
        }
    }

    private fun checkMember(memberId: Long) {
        if (this.stadium.member.memberId != memberId) {
            throw IllegalArgumentException(ExceptionMessage.STADIUM_NOT_OWNED_BY_MEMBER.text)
        }
    }

    companion object {
        fun create(
            stadium: Stadium,
            name: String,
            description: String?,
            pricePerHour: BigDecimal
        ): Court {
            return Court(stadium, name, description, pricePerHour)
        }
    }
}