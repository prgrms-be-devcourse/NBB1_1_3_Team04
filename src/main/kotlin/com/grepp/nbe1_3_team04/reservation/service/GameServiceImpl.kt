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
import org.springframework.data.domain.Pageable
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
        private const val CONFLICT_PAGE = 0
        private const val CONFLICT_PAGE_SIZE = 1
    }

    @Transactional
    override fun registerGame(member: Member, request: GameRegisterServiceRequest): GameDetailResponse {
        val memberId: Long = member.memberId ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_ABNORMAL.text)

        val reservation1: Reservation = findEntityByIdOrThrowException(
            repository = reservationRepository,
            id = request.firstReservationId,
            exceptionMessage = ExceptionMessage.RESERVATION_NOT_FOUND
        )

        val reservation2: Reservation = findEntityByIdOrThrowException(
            repository = reservationRepository,
            id = request.secondReservationId,
            exceptionMessage = ExceptionMessage.RESERVATION_NOT_FOUND
        )

        if (reservation1.member.memberId != memberId) {
            throw IllegalArgumentException(ExceptionMessage.RESERVATION_MEMBER_NOT_MATCH.text)
        }

        val game: Game = Game.create(reservation1, reservation2, GameStatus.PENDING)

        gameRepository.save(game)

        return GameDetailResponse.from(game)
    }

    @Transactional
    override fun updateGameStatus(member: Member, request: GameStatusUpdateServiceRequest): String {
        val memberId: Long = member.memberId ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_ABNORMAL.text)

        if (request.status !in listOf(GameStatus.READY, GameStatus.IGNORE)) {
            throw IllegalArgumentException(ExceptionMessage.GAME_STATUS_NOT_VALID.text)
        }

        val game: Game = findEntityByIdOrThrowException(
            repository = gameRepository,
            id = request.gameId,
            exceptionMessage = ExceptionMessage.GAME_NOT_FOUND
        )

        game.update(request.status)

        return if (request.status == GameStatus.READY) {
            val firstReservation: Reservation = findEntityByIdOrThrowException(
                repository = reservationRepository,
                id = game.firstTeamReservation.reservationId!!,
                exceptionMessage = ExceptionMessage.RESERVATION_NOT_FOUND
            )
            val secondReservation: Reservation = findEntityByIdOrThrowException(
                repository = reservationRepository,
                id = game.secondTeamReservation.reservationId!!,
                exceptionMessage = ExceptionMessage.RESERVATION_NOT_FOUND
            )

            if (firstReservation.reservationStatus != ReservationStatus.READY ||
                secondReservation.reservationStatus != ReservationStatus.READY
            ) {
                throw IllegalArgumentException(ExceptionMessage.RESERVATION_STATUS_NOT_READY.text)
            }

            val isConflict: Boolean = reservationRepository.findByMatchDateAndCourtAndReservationStatus(
                id = -1L,
                matchDate = firstReservation.matchDate,
                court = firstReservation.court,
                reservationStatus = ReservationStatus.CONFIRMED,
                pageable = PageRequest.of(CONFLICT_PAGE, CONFLICT_PAGE_SIZE)
            ).hasContent()

            if (isConflict) {
                game.update(GameStatus.IGNORE)
                firstReservation.updateStatus(ReservationStatus.CANCELLED)
                secondReservation.updateStatus(ReservationStatus.CANCELLED)
                gameRepository.softDeleteBySecondTeamReservation(secondReservation)
                ExceptionMessage.RESERVATION_CONFLICT.text
            } else {
                firstReservation.updateStatus(ReservationStatus.CONFIRMED)
                secondReservation.updateStatus(ReservationStatus.CONFIRMED)
                gameRepository.softDeleteBySecondTeamReservation(secondReservation)
                ExceptionMessage.RESERVATION_SUCCESS.text
            }
        } else {
            gameRepository.delete(game)
            "해당 매칭을 거절하였습니다."
        }
    }

    override fun findPendingGames(member: Member, reservationId: Long, page: Int): Slice<GameDetailResponse> {
        val memberId: Long = member.memberId ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_ABNORMAL.text)

        val reservation: Reservation = findEntityByIdOrThrowException(
            repository = reservationRepository,
            id = reservationId,
            exceptionMessage = ExceptionMessage.RESERVATION_NOT_FOUND
        )

        if (reservation.member.memberId != memberId) {
            throw IllegalArgumentException(ExceptionMessage.RESERVATION_MEMBER_NOT_MATCH.text)
        }

        val pageRequest: Pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "createdAt"))

        return gameRepository.findBySecondReservationAndStatus(
            reservation = reservation,
            status = GameStatus.PENDING,
            pageable = pageRequest
        ).map(GameDetailResponse::from)
    }

    private fun <T> findEntityByIdOrThrowException(
        repository: CustomGlobalRepository<T>,
        id: Long,
        exceptionMessage: ExceptionMessage
    ): T {
        return repository.findActiveById(id)
            ?: throw IllegalArgumentException(
                applyLogAndGetMessage(id, exceptionMessage)
            )
    }

    private fun applyLogAndGetMessage(id: Long, exceptionMessage: ExceptionMessage): String {
        log.warn(">>>> {} : {} <<<<", id, exceptionMessage.text)
        return exceptionMessage.text
    }
}