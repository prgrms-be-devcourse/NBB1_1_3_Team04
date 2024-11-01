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
        private val VALID_GAME_STATUSES = setOf(GameStatus.READY, GameStatus.IGNORE)
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

        val game = Game.create(reservation1, reservation2)
        gameRepository.save(game)

        return GameDetailResponse.from(game)
    }

    @Transactional
    override fun updateGameStatus(member: Member, request: GameStatusUpdateServiceRequest): String {
        val memberId = member.memberId ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_ABNORMAL.text)

        if (request.status !in VALID_GAME_STATUSES) {
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

        validateReservationStatus(firstReservation, secondReservation)

        return if (isReservationConflict(firstReservation)) {
            cancelReservations(game, firstReservation, secondReservation)
            ExceptionMessage.RESERVATION_CONFLICT.text
        } else {
            confirmReservations(firstReservation, secondReservation)
            ExceptionMessage.RESERVATION_SUCCESS.text
        }
    }

    private fun validateReservationStatus(firstReservation: Reservation, secondReservation: Reservation) {
        if (firstReservation.reservationStatus != ReservationStatus.READY || secondReservation.reservationStatus != ReservationStatus.READY) {
            throw IllegalArgumentException(ExceptionMessage.RESERVATION_STATUS_NOT_READY.text)
        }
    }

    private fun cancelReservations(game: Game, firstReservation: Reservation, secondReservation: Reservation) {
        game.update(GameStatus.IGNORE)
        firstReservation.cancel()
        secondReservation.cancel()
        gameRepository.softDeleteBySecondTeamReservation(secondReservation)
    }

    private fun confirmReservations(firstReservation: Reservation, secondReservation: Reservation) {
        firstReservation.confirm()
        secondReservation.confirm()
    }

    private fun isReservationConflict(reservation: Reservation): Boolean {
        return reservationRepository.existsByMatchDateAndCourtAndReservationStatus(
            id = reservation.reservationId!!,
            matchDate = reservation.matchDate,
            court = reservation.court,
            reservationStatus = ReservationStatus.CONFIRMED
        )
    }

    private fun getValidatedReservation(reservationId: Long, memberId: Long): Reservation {
        val reservation = findEntityByIdOrThrowException(
            reservationRepository,
            reservationId,
            ExceptionMessage.RESERVATION_NOT_FOUND
        )
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
