package com.grepp.nbe1_3_team04.reservation.service

import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.reservation.domain.Mercenary
import com.grepp.nbe1_3_team04.reservation.domain.Reservation
import com.grepp.nbe1_3_team04.reservation.repository.MercenaryRepository
import com.grepp.nbe1_3_team04.reservation.repository.ReservationRepository
import com.grepp.nbe1_3_team04.reservation.service.request.MercenaryServiceRequest
import com.grepp.nbe1_3_team04.reservation.service.response.MercenaryResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.format.DateTimeFormatter


@Service
class MercenaryServiceImpl(
    private val mercenaryRepository: MercenaryRepository,
    private val reservationRepository: ReservationRepository
) : MercenaryService {

    /**
     * 용병 게시판 생성
     * 예약자만 생성 가능
     */
    @Transactional
    override fun createMercenary(request: MercenaryServiceRequest, member: Member): MercenaryResponse {
        val reservation = getReservationByReservationId(request.reservationId)

        checkReservationCreatedBy(reservation, member)

        return MercenaryResponse(
            mercenaryRepository.save(
                Mercenary.create(
                    reservation,
                    makeDescription(request.description?:"", reservation)
                )
            )
        )
    }

    /**
     * 단일 용병 게시판 조회
     */
    @Transactional(readOnly = true)
    override fun getMercenary(mercenaryId: Long): MercenaryResponse {
        val mercenary = getMercenaryByMercenaryId(mercenaryId)

        return MercenaryResponse(mercenary)
    }

    /**
     * 용병 게시판 리스트 조회
     * 해당 용병 게시판의 예약 상태가 RECRUITING 인것만 조회
     * 리스트 및 페이징
     */
    @Transactional(readOnly = true)
    override fun getMercenaries(pageable: Pageable): Page<MercenaryResponse> {
        val mercenaries = mercenaryRepository.findAllToPage(pageable)
        return mercenaries.map(::MercenaryResponse)
    }

    /**
     * 용병 게시판 삭제
     * 팀장만 삭제 가능
     */
    @Transactional
    override fun deleteMercenary(mercenaryId: Long, member: Member): Long {
        val mercenary = getMercenaryByMercenaryId(mercenaryId)

        checkReservationCreatedBy(mercenary.reservation, member)

        mercenaryRepository.delete(mercenary)
        return mercenary.mercenaryId!!
    }


    private fun makeDescription(description: String, reservation: Reservation): String {
        return reservation.matchDate.format(DateTimeFormatter.ofPattern("'('MM'/'dd HH':'mm')'")) +
                "(" +
                reservation.court.stadium.name +
                ") " +
                description
    }

    private fun getReservationByReservationId(reservationId: Long): Reservation {
        return reservationRepository.findByReservationId(reservationId) ?: throw IllegalArgumentException(ExceptionMessage.RESERVATION_NOT_FOUND.text)
    }

    private fun getMercenaryByMercenaryId(mercenaryId: Long): Mercenary {
        return mercenaryRepository.findByMercenaryId(mercenaryId) ?: throw IllegalArgumentException(ExceptionMessage.MERCENARY_NOT_FOUND.text)
    }

    private fun checkReservationCreatedBy(reservation: Reservation, member: Member) {
        require(reservation.member.memberId == member.memberId) { ExceptionMessage.RESERVATION_NOT_MEMBER.text }
    }
}
