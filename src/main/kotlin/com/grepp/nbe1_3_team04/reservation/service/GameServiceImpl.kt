package com.grepp.nbe1_3_team04.reservation.service

import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.global.repository.CustomGlobalRepository
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.reservation.domain.Game
import com.grepp.nbe1_3_team04.reservation.domain.GameStatus
import com.grepp.nbe1_3_team04.reservation.domain.Reservation
import com.grepp.nbe1_3_team04.reservation.domain.ReservationStatus
import com.grepp.nbe1_3_team04.reservation.repository.GameRepository
import com.grepp.nbe1_3_team04.reservation.repository.ReservationRepository
import com.grepp.nbe1_3_team04.reservation.service.request.GameRegisterServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.request.GameStatusUpdateServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.response.GameDetailResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GameServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val gameRepository: GameRepository
) : GameService {

    companion object {
        private val log = LoggerFactory.getLogger(GameServiceImpl::class.java)
        private const val PAGE_SIZE = 10
    }

    @Transactional
    override fun registerGame(member: Member, request: GameRegisterServiceRequest): GameDetailResponse {
        val memberId = member.memberId ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_ABNORMAL.text)

        val reservation1 = getValidatedReservation(request.firstReservationId, memberId)
        val reservation2 = findEntityByIdOrThrowException(
            reservationRepository,
            request.secondReservationId,
            ExceptionMessage.RESERVATION_NOT_FOUND
        )

        val game = Game.create(reservation1, reservation2, GameStatus.PENDING)
        gameRepository.save(game)

        return GameDetailResponse.from(game)
    }

    @Transactional
    override fun updateGameStatus(member: Member, request: GameStatusUpdateServiceRequest): String {
        val memberId = member.memberId ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_ABNORMAL.text)

        if (request.status !in listOf(GameStatus.READY, GameStatus.IGNORE)) {
            throw IllegalArgumentException(ExceptionMessage.GAME_STATUS_NOT_VALID.text)
        }

        val game = findEntityByIdOrThrowException(gameRepository, request.gameId, ExceptionMessage.GAME_NOT_FOUND)
        game.update(request.status)

        return if (request.status == GameStatus.READY) {
            confirmReservations(game)
        } else {
            gameRepository.delete(game)
            "해당 매칭을 거절하였습니다."
        }
    }

    override fun findPendingGames(member: Member, reservationId: Long, page: Int): Slice<GameDetailResponse> {
        val memberId = member.memberId ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_ABNORMAL.text)

        val reservation = getValidatedReservation(reservationId, memberId)
        val pageRequest = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "createdAt"))

        return gameRepository.findBySecondReservationAndStatus(reservation, GameStatus.PENDING, pageRequest)
            .map(GameDetailResponse::from)
    }

    private fun confirmReservations(game: Game): String {
        val firstReservation = findEntityByIdOrThrowException(
            reservationRepository,
            game.firstTeamReservation.reservationId!!,
            ExceptionMessage.RESERVATION_NOT_FOUND
        )
        val secondReservation = findEntityByIdOrThrowException(
            reservationRepository,
            game.secondTeamReservation.reservationId!!,
            ExceptionMessage.RESERVATION_NOT_FOUND
        )

        if (firstReservation.reservationStatus != ReservationStatus.READY || secondReservation.reservationStatus != ReservationStatus.READY) {
            throw IllegalArgumentException(ExceptionMessage.RESERVATION_STATUS_NOT_READY.text)
        }

        return if (isReservationConflict(firstReservation)) {
            game.update(GameStatus.IGNORE)
            firstReservation.updateStatus(ReservationStatus.CANCELLED)
            secondReservation.updateStatus(ReservationStatus.CANCELLED)
            gameRepository.softDeleteBySecondTeamReservation(secondReservation)
            ExceptionMessage.RESERVATION_CONFLICT.text
        } else {
            firstReservation.updateStatus(ReservationStatus.CONFIRMED)
            secondReservation.updateStatus(ReservationStatus.CONFIRMED)
            ExceptionMessage.RESERVATION_SUCCESS.text
        }
    }

    private fun isReservationConflict(reservation: Reservation): Boolean {
        return reservationRepository.findByMatchDateAndCourtAndReservationStatus(
            id = -1L,
            matchDate = reservation.matchDate,
            court = reservation.court,
            reservationStatus = ReservationStatus.CONFIRMED,
            pageable = PageRequest.of(0, 1)
        ).hasContent()
    }

    private fun getValidatedReservation(reservationId: Long, memberId: Long): Reservation {
        val reservation =
            findEntityByIdOrThrowException(reservationRepository, reservationId, ExceptionMessage.RESERVATION_NOT_FOUND)
        if (reservation.member.memberId != memberId) {
            throw IllegalArgumentException(ExceptionMessage.RESERVATION_MEMBER_NOT_MATCH.text)
        }
        return reservation
    }

    private fun <T> findEntityByIdOrThrowException(
        repository: CustomGlobalRepository<T>,
        id: Long,
        exceptionMessage: ExceptionMessage
    ): T {
        return repository.findActiveById(id)
            ?: throw IllegalArgumentException(applyLogAndGetMessage(id, exceptionMessage))
    }

    private fun applyLogAndGetMessage(id: Long, exceptionMessage: ExceptionMessage): String {
        log.warn(">>>> {} : {} <<<<", id, exceptionMessage.text)
        return exceptionMessage.text
    }
}