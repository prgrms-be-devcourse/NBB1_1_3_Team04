package com.grepp.nbe1_3_team04.team.domain

import com.grepp.nbe1_3_team04.global.domain.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete


@SQLDelete(sql = "UPDATE team_rate SET is_deleted = TRUE WHERE team_rate_id = ?")
@Entity
class TeamRate private constructor(
    team: Team,
    rating: Double,
    evaluation: String?
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val teamRateId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    val team: Team = team

    @Column(nullable = false)
    var rating: Double = rating
        protected set

    @Column(length = 200, nullable = true)
    var evaluation: String? = evaluation
        protected set

    companion object{
        fun create(
            team: Team,
            rating: Double,
            evaluation: String?
        ): TeamRate {
            return TeamRate(
                team = team,
                rating = rating,
                evaluation = evaluation
            )
        }
    }
}