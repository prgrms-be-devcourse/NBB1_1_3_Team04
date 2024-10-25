package com.grepp.nbe1_3_team04.team.domain

import com.grepp.nbe1_3_team04.global.domain.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete


@SQLDelete(sql = "UPDATE team SET is_deleted = 'TRUE' WHERE team_id = ?")
@Entity
class Team private constructor(
    stadiumId: Long?,
    name: String,
    description: String?,
    totalRecord: TotalRecord,
    location: String?
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val teamId: Long? = null

    @Column(nullable = true)
    var stadiumId: Long? = stadiumId
        protected set

    @Column(length = 50, nullable = false)
    var name: String = name
        protected set

    @Column(length = 200, nullable = true)
    var description: String? = description
        protected set

    @Embedded
    var totalRecord: TotalRecord = totalRecord
        protected set

    @Column(length = 100, nullable = true)
    var location: String? = location
        protected set

    fun updateName(name: String) {
        this.name = name
    }

    fun updateDescription(description: String) {
        this.description = description
    }

    fun updateLocation(location: String) {
        this.location = location
    }

    companion object {
        fun create(
            stadiumId: Long?,
            name: String,
            description: String?,
            totalRecord: TotalRecord,
            location: String?
        ): Team {
            return Team(
                stadiumId = stadiumId,
                name = name,
                description = description,
                totalRecord = totalRecord,
                location = location
            )

        }
    }
}