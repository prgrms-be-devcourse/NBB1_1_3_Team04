package com.grepp.nbe1_3_team04.team.service

import com.grepp.nbe1_3_team04.chat.service.event.TeamMemberJoinEvent
import com.grepp.nbe1_3_team04.chat.service.event.TeamMemberLeaveEvent
import com.grepp.nbe1_3_team04.chat.service.event.TeamMembersJoinEvent
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import com.grepp.nbe1_3_team04.team.domain.Team
import com.grepp.nbe1_3_team04.team.domain.TeamMember
import com.grepp.nbe1_3_team04.team.domain.TeamMemberRole
import com.grepp.nbe1_3_team04.team.repository.TeamMemberRepository
import com.grepp.nbe1_3_team04.team.repository.TeamRepository
import com.grepp.nbe1_3_team04.team.service.request.TeamMemberServiceRequest
import com.grepp.nbe1_3_team04.team.service.response.TeamResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeamMemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val teamRepository: TeamRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val publisher: ApplicationEventPublisher
) : TeamMemberService {

    @Transactional
    override fun addTeamMembers(teamId: Long, request: TeamMemberServiceRequest): List<TeamResponse> {
        //팀원 추가할 팀 찾기
        val team: Team = findTeamByIdOrThrowException(teamId)

        //return할 DTO
        val teamMembers: MutableList<TeamResponse> = ArrayList<TeamResponse>()
        //채팅방에 초대할 TeamMember List
        val teamMemberList: MutableList<TeamMember> = ArrayList<TeamMember>()

        //member 추가
        for (email in request.emails) {
            val member: Member = memberRepository.findByEmail(email) ?: continue
            var teamMember: TeamMember? =
                teamMemberRepository.findByTeamIdAndMemberId(teamId, member.memberId!!)

            //해당 멤버가 팀에 이미 존재 할 경우
            if (teamMember != null) continue

            teamMember = TeamMember.createMember(team, member)
            teamMembers.add(TeamResponse(teamMemberRepository.save(teamMember)))
            teamMemberList.add(teamMember)
        }
        // 팀 멤버 채팅방 초대
        if (teamMemberList.isEmpty()) {
            return teamMembers
        }
        if (teamMemberList.size == 1) {
            publisher.publishEvent(TeamMemberJoinEvent(teamMemberList[0].member, team.teamId!!))
        } else {
            publisher.publishEvent(TeamMembersJoinEvent(teamMemberList, team.teamId!!))
        }

        return teamMembers
    }

    @Transactional(readOnly = true)
    override fun getTeamMembers(teamId: Long): List<TeamResponse> {
        //팀 찾기
        val team: Team = findTeamByIdOrThrowException(teamId)

        return teamMemberRepository.findTeamMembersByTeam(team)
            .map{ TeamResponse(it) }
    }

    //팀 탈퇴_팀장
    @Transactional
    override fun deleteTeamMemberByCreator(teamId: Long, teamMemberId: Long, member: Member): Long {
        val memberId : Long = member.memberId ?: throw
            IllegalArgumentException("존재하지 않는 회원입니다.")
        //삭제할 팀 멤버 찾기
        val teamMember: TeamMember = findTeamMemberByIdOrThrowException(teamMemberId)
        //현재 유저 정보
        val creator: TeamMember = findByTeamIdAndMemberIdOrThrowException(teamId, memberId)

        require(creator.role == TeamMemberRole.CREATOR) { "삭제 권한이 없습니다" }

        teamMemberRepository.delete(teamMember)
        //팀 멤버 삭제시 해당 멤버 채팅방 퇴장 이벤트 처리
        publisher.publishEvent(TeamMemberLeaveEvent(teamMember.member, teamMember.team.teamId!!))
        return teamMemberId
    }

    //팀 탈퇴_본인
    @Transactional
    override fun deleteTeamMember(teamId: Long, member: Member): Long {
        val memberId : Long = member.memberId ?: throw
                IllegalArgumentException("존재하지 않는 회원입니다.")
        val teamMember: TeamMember = findByTeamIdAndMemberIdOrThrowException(teamId, memberId)
        teamMemberRepository.delete(teamMember)
        //팀 멤버 삭제시 해당 멤버 채팅방 퇴장 이벤트 처리
        publisher.publishEvent(TeamMemberLeaveEvent(teamMember.member, teamMember.team.teamId!!))
        return teamMember.teamMemberId!!
    }


    fun findTeamByIdOrThrowException(id: Long): Team {
        val team: Team = teamRepository.findByTeamId(id)?: throw
            IllegalArgumentException("해당 팀이 존재하지 않습니다.")

        return team
    }

    fun findTeamMemberByIdOrThrowException(id: Long): TeamMember {
        val teamMember: TeamMember = teamMemberRepository.findByTeamMemberId(id)?: throw
                IllegalArgumentException("존재하지 않는 팀원입니다.")
        return teamMember
    }

    fun findByTeamIdAndMemberIdOrThrowException(teamId: Long, memberId: Long): TeamMember {
        val teamMember: TeamMember = teamMemberRepository.findByTeamIdAndMemberId(teamId, memberId)?: throw
                IllegalArgumentException("존재하지 않는 팀원입니다.")
        return teamMember
    }
}
