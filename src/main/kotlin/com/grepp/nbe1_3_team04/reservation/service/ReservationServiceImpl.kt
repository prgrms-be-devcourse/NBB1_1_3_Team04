package com.grepp.nbe1_3_team04.reservation.service

import com.grepp.nbe1_3_team04.chat.service.event.ReservationDeletedEvent
import com.grepp.nbe1_3_team04.chat.service.event.ReservationMembersJoinEvent
import com.grepp.nbe1_3_team04.chat.service.event.ReservationPublishedEvent
import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.member.domain.Gender
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import com.grepp.nbe1_3_team04.reservation.domain.*
import com.grepp.nbe1_3_team04.reservation.repository.GameRepository
import com.grepp.nbe1_3_team04.reservation.repository.MercenaryRepository
import com.grepp.nbe1_3_team04.reservation.repository.ParticipantRepository
import com.grepp.nbe1_3_team04.reservation.repository.ReservationRepository
import com.grepp.nbe1_3_team04.reservation.service.request.ReservationUpdateServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.response.ReservationInfoDetailsResponse
import com.grepp.nbe1_3_team04.reservation.service.response.ReservationInfoResponse
import com.grepp.nbe1_3_team04.reservation.service.response.ReservationsResponse
import com.grepp.nbe1_3_team04.stadium.domain.Court
import com.grepp.nbe1_3_team04.stadium.repository.CourtRepository
import com.grepp.nbe1_3_team04.team.domain.Team
import com.grepp.nbe1_3_team04.team.repository.TeamRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ReservationServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val courtRepository: CourtRepository,
    private val memberRepository: MemberRepository,
    private val teamRepository: TeamRepository,
    private val participantRepository: ParticipantRepository,
    private val mercenaryRepository: MercenaryRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val gameRepository: GameRepository
) :
    ReservationService {

    @Transactional(readOnly = true)
    override fun findReadyReservations(reservationId: Long, page: Int): Slice<ReservationsResponse> {
        val pageRequest: PageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "createdAt"))
        val reservation: Reservation =
            reservationRepository.findActiveById(reservationId) ?: throw IllegalArgumentException("존재하지 않는 예약입니다.")

        if (reservation.reservationStatus !== ReservationStatus.READY) {
            throw IllegalArgumentException(ExceptionMessage.RESERVATION_STATUS_NOT_READY.text)
        }

        return reservationRepository.findByMatchDateAndCourtAndReservationStatus(
            reservationId, reservation.matchDate, reservation.court, ReservationStatus.READY, pageRequest
        )
            .map(ReservationsResponse::from)
    }

    @Transactional
    override fun createReservation(
        memberId: Long,
        courtId: Long,
        teamId: Long,
        matchDate: LocalDateTime,
        memberIds: List<Long>
    ) {
        val court: Court = courtRepository.findActiveById(courtId) ?: throw IllegalArgumentException("해당하는 구장이 없습니다.")
        val member: Member =
            memberRepository.findActiveById(memberId) ?: throw IllegalArgumentException("해당하는 회원이 없습니다.")
        val team: Team = teamRepository.findByTeamId(teamId) ?: throw IllegalArgumentException("해당하는 팀이 없습니다.")

        val participantMembers: List<Member> = memberRepository.findAllById(memberIds)

        require(participantMembers.size == memberIds.size) { "해당하는 회원이 없습니다." }

        val reservation: Reservation = createReservationOf(court, member, team, matchDate, participantMembers)
        val savedReservation: Reservation = reservationRepository.save(reservation)

        val participants: List<Participant> = createParticipantsOf(savedReservation, participantMembers)
        participantRepository.saveAll(participants)

        if (memberIds.size < 6) {
            val mercenary: Mercenary = Mercenary.createDefault(reservation)
            mercenaryRepository.save(mercenary)
        }

        publishChatEventsOf(savedReservation, participants)
    }

    private fun createReservationOf(
        court: Court,
        member: Member,
        team: Team,
        matchDate: LocalDateTime,
        participantMembers: List<Member>
    ): Reservation {
        val gender: ParticipantGender = classifyGenderBy(participantMembers)

        if (participantMembers.size >= 6) {
            return Reservation.createReadyReservation(court, member, team, gender, matchDate)
        }
        return Reservation.createRecruitReservation(court, member, team, gender, matchDate)
    }

    private fun classifyGenderBy(participantMembers: List<Member>): ParticipantGender {
        if (participantMembers.stream().allMatch { m: Member -> m.gender === Gender.MALE }) {
            return ParticipantGender.MALE
        }
        if (participantMembers.stream().allMatch { m: Member -> m.gender === Gender.FEMALE }) {
            return ParticipantGender.FEMALE
        }
        return ParticipantGender.MIXED
    }

    private fun createParticipantsOf(reservation: Reservation, participantMembers: List<Member>): List<Participant> {
        return participantMembers
            .map { participantMember: Member ->
                Participant.create(
                    reservation,
                    participantMember,
                    ParticipantRole.MEMBER
                )
            }
    }

    private fun publishChatEventsOf(reservation: Reservation, participants: List<Participant>) {
        eventPublisher.publishEvent(ReservationPublishedEvent("예약 채팅방", reservation.reservationId!!))
        eventPublisher.publishEvent(ReservationMembersJoinEvent(participants, reservation.reservationId!!))
    }

    @Transactional(readOnly = true)
    override fun getTeamReservationInfo(teamId: Long): List<ReservationInfoResponse> {
        val reservations: List<Reservation> = findByTeamTeamIdOrThrowException(teamId)

        return reservations
            .map(ReservationInfoResponse::from)
    }

    @Transactional(readOnly = true)
    override fun getTeamReservationInfoDetails(reservationId: Long): ReservationInfoDetailsResponse {
        val reservation: Reservation =
            reservationRepository.findActiveById(reservationId) ?: throw IllegalArgumentException("해당 예약을 찾을 수 없습니다.")

        val participants: List<Participant> = participantRepository.findParticipantsByReservationId(reservationId)

        //상대팀 조회
        val matchedTeam: Reservation? = gameRepository.findFirstTeamReservationBySecondTeamReservationId(reservationId)
        //상대팀 이름 --> 없으면 null
        val matchTeamName: String = matchedTeam?.team?.name ?: "상대팀이 존재하지 않습니다."

        return ReservationInfoDetailsResponse.of(reservation, participants, matchTeamName)
    }

    fun findByTeamTeamIdOrThrowException(teamId: Long): List<Reservation> {
        val result: List<Reservation> = reservationRepository.findByTeamTeamId(teamId)
        require(result.isNotEmpty()) { "해당 팀이 존재하지 않습니다." }
        return result
    }

    @Transactional
    override fun deleteReservation(reservationId: Long, member: Member): Long {
        val reservation: Reservation =
            reservationRepository.findActiveById(reservationId) ?: throw IllegalArgumentException("존재하지 않는 예약입니다.")

        require(reservation.reservationStatus === ReservationStatus.RECRUITING) { "취소할 수 없는 예약 입니다." }

        require(reservation.member.memberId === member.memberId) { "예약한 사람만이 취소할 수 있습니다." }

        deleteGames(reservationId)
        deleteMercenaries(reservationId)
        deleteParticipants(reservationId)

        reservationRepository.delete(reservation)
        eventPublisher.publishEvent(ReservationDeletedEvent(reservationId))

        return reservationId
    }

    @Transactional
    fun deleteGames(reservationId: Long) {
        val games: List<Game> = gameRepository.findAllByReservationId(reservationId)
        gameRepository.deleteAllInBatch(games)
    }

    @Transactional
    fun deleteMercenaries(reservationId: Long) {
        val mercenaries: List<Mercenary> = mercenaryRepository.findAllMercenaryByReservationId(reservationId)
        mercenaryRepository.deleteAllInBatch(mercenaries)
    }

    @Transactional
    fun deleteParticipants(reservationId: Long) {
        val participants: List<Participant> = participantRepository.findAllByReservationId(reservationId)
        participantRepository.deleteAllInBatch(participants)
    }

    /**
     * 매칭 예약 상태 변경 API
     * 예약 방장만 상태 변경 가능
     */
    @Transactional
    override fun changeStatus(request: ReservationUpdateServiceRequest, member: Member): ReservationInfoResponse {
        val reservation: Reservation = reservationRepository.findActiveById(request.reservationId)
            ?: throw IllegalArgumentException("존재하지 않는 예약입니다.")

        reservation.checkReservationOwner(member.memberId!!)

        reservation.updateStatus(request.status)

        return ReservationInfoResponse.from(reservation)
    }
}