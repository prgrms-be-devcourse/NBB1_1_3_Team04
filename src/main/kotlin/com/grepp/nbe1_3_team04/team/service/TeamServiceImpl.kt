package com.grepp.nbe1_3_team04.team.service

import com.grepp.nbe1_3_team04.chat.service.event.TeamDeletedEvent
import com.grepp.nbe1_3_team04.chat.service.event.TeamPublishedEvent
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.team.domain.*
import com.grepp.nbe1_3_team04.team.repository.TeamMemberRepository
import com.grepp.nbe1_3_team04.team.repository.TeamRateRepository
import com.grepp.nbe1_3_team04.team.repository.TeamRepository
import com.grepp.nbe1_3_team04.team.service.request.TeamDefaultServiceRequest
import com.grepp.nbe1_3_team04.team.service.response.TeamDefaultResponse
import com.grepp.nbe1_3_team04.team.service.response.TeamInfoResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class TeamServiceImpl(
    private val teamRepository: TeamRepository,
    private val teamRateRepository: TeamRateRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val publisher: ApplicationEventPublisher,
) : TeamService {

    @Transactional
    override fun createTeam(dto: TeamDefaultServiceRequest, member: Member): TeamDefaultResponse {

        val stadiumId: Long? = null

        //dto -> entity
        val entity = Team.create(
            stadiumId = stadiumId,
            name = requireNotNull(dto.name) {"팀 이름은 필수입니다."},
            description = dto.description,
            totalRecord = TotalRecord(),    //초기값으로 생성
            location = dto.location
        )
        val createdTeam: Team = teamRepository.save(entity)

        //채팅방 생성 이벤트 실행
        publisher.publishEvent(TeamPublishedEvent(createdTeam.name, createdTeam.teamId!!))

        teamMemberRepository.save(TeamMember.createCreator(createdTeam, member))

        return TeamDefaultResponse(createdTeam)
    }


    @Transactional(readOnly = true)
    override fun getTeamInfo(teamId: Long): TeamInfoResponse {
        //팀 정보

        val teamEntity: Team = findTeamByIdOrThrowException(teamId)

        //팀 평가 ->  List
        val evaluations = teamRateRepository.findEvaluationsByTeam(teamEntity)
            .map{it.evaluation ?: ""}

        val maleCount = teamRepository.countMaleByMemberId(teamId)
        val femaleCount = teamRepository.countFemaleByMemberId(teamId)

        return TeamInfoResponse(
            teamEntity,
            evaluations,
            maleCount,
            femaleCount
        )
    }

    @Transactional
    override fun updateTeamInfo(teamId: Long, dto: TeamDefaultServiceRequest, member: Member): TeamDefaultResponse {
        val memberId : Long = member.memberId ?: throw
                IllegalArgumentException("존재하지 않는 회원입니다.")
        //변경할 팀 id로 검색
        val teamEntity: Team = findTeamByIdOrThrowException(teamId)
        //현재 유저 정보 검색
        val teamMember: TeamMember = findTeamMemberByIdOrThrowException(teamId, memberId)
        //권한 정보
        checkAuthority(teamId, teamMember)

        //entity에 수정된 값 적용
        dto.name?.let{teamEntity.updateName(it)}
        dto.description?.let{teamEntity.updateName(it)}
        dto.location?.let{teamEntity.updateName(it)}

        //바뀐 Team값 반환
        return TeamDefaultResponse(teamEntity)
    }

    @Transactional
    override fun deleteTeam(teamId: Long, member: Member): Long {
        val memberId : Long = member.memberId ?: throw
            IllegalArgumentException("존재하지 않는 회원입니다.")
        //삭제할 팀 탐색
        val teamEntity: Team = findTeamByIdOrThrowException(teamId)
        //현재 유저 정보 검색
        val teamMember: TeamMember = findTeamMemberByIdOrThrowException(teamId, memberId)
        //권한 정보
        checkAuthority(teamId, teamMember)

        teamRepository.delete(teamEntity)
        // 채팅방 삭제 이벤트 실행
        publisher.publishEvent(TeamDeletedEvent(teamId))
        return teamId
    }


    fun findTeamByIdOrThrowException(id: Long): Team {
        val team: Team = teamRepository.findByTeamId(id) ?: throw
                IllegalArgumentException("해당 팀이 존재하지 않습니다.")
        return team
    }

    fun findTeamMemberByIdOrThrowException(teamId: Long, memberId: Long): TeamMember {
        val teamMember: TeamMember = teamMemberRepository.findByTeamIdAndMemberId(teamId, memberId) ?: throw
                IllegalArgumentException("존재하지 않는 팀원입니다.")
        return teamMember
    }

    fun checkAuthority(teamId: Long, teamMember: TeamMember) {
        require(
            !(teamMember.team.teamId !== teamId || teamMember.role !== TeamMemberRole.CREATOR)
        ) { "접근 권한이 없습니다." }
    }
}
