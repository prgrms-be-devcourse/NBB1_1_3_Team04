package com.grepp.nbe1_3_team04.reservation.service

import com.grepp.nbe1_3_team04.chat.service.event.ReservationMemberJoinEvent
import com.grepp.nbe1_3_team04.chat.service.event.ReservationMemberLeaveEvent
import com.grepp.nbe1_3_team04.chat.service.event.ReservationMembersJoinEvent
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.reservation.domain.Mercenary
import com.grepp.nbe1_3_team04.reservation.domain.Participant
import com.grepp.nbe1_3_team04.reservation.domain.ParticipantRole
import com.grepp.nbe1_3_team04.reservation.domain.Reservation
import com.grepp.nbe1_3_team04.reservation.repository.MercenaryRepository
import com.grepp.nbe1_3_team04.reservation.repository.ParticipantRepository
import com.grepp.nbe1_3_team04.reservation.repository.ReservationRepository
import com.grepp.nbe1_3_team04.reservation.service.request.ParticipantUpdateServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.response.ParticipantResponse
import com.grepp.nbe1_3_team04.team.domain.Team
import com.grepp.nbe1_3_team04.team.domain.TeamMember
import com.grepp.nbe1_3_team04.team.repository.TeamMemberRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class ParticipantServiceImpl(
    private val mercenaryRepository: MercenaryRepository,
    private val reservationRepository: ReservationRepository,
    private val participantRepository: ParticipantRepository,
    private val teamMemberRepository: TeamMemberRepository,
    private val publisher: ApplicationEventPublisher
) : ParticipantService {


    /**
     * 용병 추가
     * 해당 용병 게시판의 예약에 참가한 인원이면 추가되지 않게 예외처리
     * role은 Pending으로
     */
    @Transactional
    override fun createMercenaryParticipant(mercenaryId: Long, member: Member): ParticipantResponse {
        val mercenary = getMercenaryByMercenaryId(mercenaryId)

        checkParticipantMercenaryByReservationIdAndMemberId(mercenary.reservation.reservationId!!, member.memberId!!)

        return ParticipantResponse(
            participantRepository.save(
                Participant.create(
                    mercenary.reservation,
                    member,
                    ParticipantRole.PENDING
                )
            )
        )
    }

    /**
     * 멤버 추가
     * 해당 예약 팀의 팀원이면 추가 가능
     * 채팅방에도 추가
     */
    @Transactional
    override fun createParticipant(reservationId: Long, member: Member): ParticipantResponse {
        val reservation = getReservationByReservationId(reservationId)

        getTeamMember(reservation.team, member)

        checkParticipantByReservationIdAndMemberId(reservation.reservationId!!, member.memberId!!)

        publisher.publishEvent(ReservationMemberJoinEvent(member, reservationId))

        return ParticipantResponse(
            participantRepository.save(
                Participant.create(
                    reservation,
                    member,
                    ParticipantRole.MEMBER
                )
            )
        )
    }

    /**
     * 멤버 리스트 한번에 추가
     * 투표가 끝나고 예약이 생성되면 투표 진행한 사람 추가
     * 채팅방에도 추가해주기
     */
    @Transactional
    fun createParticipants(reservationId: Long, members: List<Member>) {
        val reservation = getReservationByReservationId(reservationId)

        val participants: MutableList<Participant> = ArrayList()
        for (member in members) {
            participants.add(
                Participant.create(
                    reservation,
                    member, ParticipantRole.MEMBER
                )
            )
        }

        participantRepository.saveAll(participants)

        publisher.publishEvent(ReservationMembersJoinEvent(participants, reservationId))
    }

    /**
     * 예약 삭제에 따른 예약 인원 전체 삭제
     * 채팅방에서도 삭제는 예약(채팅방) 삭제 이벤트로 발생함
     */
    @Transactional
    fun deleteParticipants(reservationId: Long) {
        val participants = participantRepository.findParticipantsByReservationId(reservationId)

        participantRepository.deleteAll(participants)
    }

    /**
     * 예약 인원 삭제
     * 예약한 사람만 할 수 있게 (Member는 로그인 한 사람)
     * Accept or Member인 사람은 채팅방에서도 삭제
     * 7명 이상일 때에만 취소 가능하게 구현
     */
    @Transactional
    override fun deleteParticipant(reservationId: Long, member: Member): Long {
        val participants = participantRepository.findParticipantsByReservationId(reservationId)

        require(participants.size >= 7) { ExceptionMessage.PARTICIPANT_NOT_MEMBER.text }

        val participant = participants.firstOrNull { it.member.memberId == member.memberId }
            ?: throw IllegalArgumentException(ExceptionMessage.PARTICIPANT_NOT_IN_MEMBER.text)

        participantRepository.delete(participant)

        if (participant.participantRole == ParticipantRole.MEMBER || participant.participantRole == ParticipantRole.ACCEPT) {
            publisher.publishEvent(ReservationMemberLeaveEvent(member, reservationId))
        }

        return member.memberId!!
    }

    /**
     * 용병의 상태 변경
     * 권한은 해당 참여 인원의 예약자만 가능
     * Accept or ignore
     * Accept시 예약채팅방에도 추가
     */
    @Transactional
    override fun updateMercenaryParticipant(
        request: ParticipantUpdateServiceRequest,
        member: Member
    ): ParticipantResponse {
        val participant = getParticipantByParticipantId(request.participantId)

        checkReservationCreatedBy(participant.reservation, member)

        require(participant.participantRole != request.role) { ExceptionMessage.SAME_PARTICIPANT_ROLE.text }

        participant.updateRole(request.role)

        if (request.role == ParticipantRole.ACCEPT) {
            publisher.publishEvent(ReservationMemberJoinEvent(member, participant.reservation.reservationId!!))
        }

        return ParticipantResponse(participantRepository.save(participant))
    }

    /**
     * 실제로 참여하는 예약 참여자 조회
     * agree, member인 참여자 조회
     */
    @Transactional(readOnly = true)
    override fun getAcceptParticipants(reservationId: Long): List<ParticipantResponse> {
        val participants = participantRepository.findParticipantByReservationIdAndRole(reservationId)

        return participants.map { ParticipantResponse(it) }
    }

    /**
     * 전체 예약 참여자 조회
     * Pending, Ingore인 사람도 조회
     */
    @Transactional(readOnly = true)
    override fun getParticipants(reservationId: Long): List<ParticipantResponse> {
        val participants = participantRepository.findParticipantsByReservationId(reservationId)

        return participants.map { ParticipantResponse(it) }
    }

    /**
     * 용병 예약 참여자 조회
     * pending인 상태인 참여자 조회
     */
    @Transactional(readOnly = true)
    override fun getParticipantsMercenary(reservationId: Long): List<ParticipantResponse> {
        val participants = participantRepository.findParticipantMercenaryByReservationId(reservationId)

        return participants.map { ParticipantResponse(it) }
    }

    private fun getReservationByReservationId(reservationId: Long): Reservation {
        return reservationRepository.findByReservationId(reservationId) ?: throw IllegalArgumentException(ExceptionMessage.RESERVATION_NOT_FOUND.text)
    }

    private fun getMercenaryByMercenaryId(mercenaryId: Long): Mercenary {
        return mercenaryRepository.findByMercenaryId(mercenaryId) ?: throw IllegalArgumentException(ExceptionMessage.MERCENARY_NOT_FOUND.text)
    }

    private fun getParticipantByParticipantId(participantId: Long): Participant {
        return participantRepository.findParticipantsByParticipantId(participantId) ?: throw IllegalArgumentException(ExceptionMessage.PARTICIPANT_NOT_IN_MEMBER.text)
    }

    private fun getTeamMember(team: Team, member: Member): TeamMember {
        return teamMemberRepository.findByTeamIdAndMemberId(team.teamId!!, member.memberId!!) ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_NOT_IN_TEAM.text)
    }

    private fun checkReservationCreatedBy(reservation: Reservation, member: Member) {
        require(reservation.member.memberId == member.memberId) { ExceptionMessage.RESERVATION_NOT_MEMBER.text }
    }

    private fun checkParticipantByReservationIdAndMemberId(reservationId: Long, memberId: Long) {
        require(
            participantRepository.findParticipantsByReservationIdAndMemberId(reservationId, memberId) == null
        ) { ExceptionMessage.PARTICIPANT_IN_MEMBER.text }
    }

    private fun checkParticipantMercenaryByReservationIdAndMemberId(reservationId: Long, memberId: Long) {
        require(
            participantRepository.findParticipantsByReservationIdAndMemberId(reservationId, memberId) == null
        ) { ExceptionMessage.MERCENARY_IN_RESERVATION.text }
    }
}
