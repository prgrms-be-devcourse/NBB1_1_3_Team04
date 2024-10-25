package com.grepp.nbe1_3_team04.team.domain

import com.grepp.nbe1_3_team04.global.domain.BaseEntity
import com.grepp.nbe1_3_team04.member.domain.Member
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.SQLDelete


@SQLDelete(sql = "UPDATE team_member SET is_deleted = 'TRUE' WHERE team_member_id = ?")
@Entity
class TeamMember private constructor(
    team: Team,
    member: Member,
    role: TeamMemberRole
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val teamMemberId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    val team: Team = team

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member = member

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: TeamMemberRole = role

    companion object {
        fun create(
            team: Team,
            member: Member,
            role: TeamMemberRole
        ): TeamMember {
            return TeamMember(
                team = team,
                member = member,
                role = role
            )
        }

        fun createCreator(
            team: Team,
            member: Member
        ): TeamMember {
            return TeamMember(
                team = team,
                member = member,
                role = TeamMemberRole.CREATOR
            )
        }

        fun createMember(
            team: Team,
            member: Member
        ): TeamMember {
            return TeamMember(
                team = team,
                member = member,
                role = TeamMemberRole.MEMBER
            )
        }
    }
}
