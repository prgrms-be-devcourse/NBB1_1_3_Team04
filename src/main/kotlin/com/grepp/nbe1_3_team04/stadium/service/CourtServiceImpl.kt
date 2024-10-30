package com.grepp.nbe1_3_team04.stadium.service

import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.global.repository.CustomGlobalRepository
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import com.grepp.nbe1_3_team04.stadium.domain.Court
import com.grepp.nbe1_3_team04.stadium.domain.Stadium
import com.grepp.nbe1_3_team04.stadium.repository.CourtRepository
import com.grepp.nbe1_3_team04.stadium.repository.StadiumRepository
import com.grepp.nbe1_3_team04.stadium.service.request.CourtDeleteServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.request.CourtRegisterServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.request.CourtUpdateServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.response.CourtDetailResponse
import com.grepp.nbe1_3_team04.stadium.service.response.CourtsResponse
import com.grepp.nbe1_3_team04.stadium.util.SortFieldMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourtServiceImpl(
    private val courtRepository: CourtRepository,
    private val stadiumRepository: StadiumRepository,
    private val memberRepository: MemberRepository
) : CourtService {

    companion object {
        private val log = LoggerFactory.getLogger(CourtServiceImpl::class.java)
    }

    override fun getCourtsByStadiumId(stadiumId: Long, page: Int, sort: String): Slice<CourtsResponse> {
        findEntityByIdOrThrowException<Stadium>(
            repository = stadiumRepository,
            id = stadiumId,
            exceptionMessage = ExceptionMessage.STADIUM_NOT_FOUND
        )

        val pageable: Pageable = PageRequest.of(page, 10, Sort.by(SortFieldMapper.getDatabaseField(sort)))
        val courts: Slice<Court> = courtRepository.findByStadium_StadiumId(stadiumId, pageable)
        return courts.map(CourtsResponse::from)
    }

    override fun getAllCourts(page: Int, sort: String): Slice<CourtsResponse> {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by(SortFieldMapper.getDatabaseField(sort)))
        val courts: Slice<Court> = courtRepository.findAllActive(pageable)
        return courts.map(CourtsResponse::from)
    }

    override fun getCourtByCourtId(courtId: Long): CourtDetailResponse {
        val court = findEntityByIdOrThrowException<Court>(
            repository = courtRepository,
            id = courtId,
            exceptionMessage = ExceptionMessage.COURT_NOT_FOUND
        )
        return CourtDetailResponse.from(court)
    }

    @Transactional
    override fun registerCourt(request: CourtRegisterServiceRequest, member: Member): CourtDetailResponse {
        val memberId: Long = member.memberId ?: throw IllegalArgumentException("Member ID는 null일 수 없습니다.")

        val stadium = validateStadiumOwnership(request.stadiumId, memberId)

        val court = Court.create(
            stadium = stadium,
            name = request.name,
            description = request.description,
            pricePerHour = request.pricePerHour
        )
        courtRepository.save(court)

        return CourtDetailResponse.from(court)
    }

    @Transactional
    override fun updateCourt(request: CourtUpdateServiceRequest, member: Member, courtId: Long): CourtDetailResponse {
        val memberId: Long = member.memberId ?: throw IllegalArgumentException("Member ID는 null일 수 없습니다.")

        validateStadiumOwnership(request.stadiumId, memberId)

        val court = findEntityByIdOrThrowException<Court>(
            repository = courtRepository,
            id = courtId,
            exceptionMessage = ExceptionMessage.COURT_NOT_FOUND
        )

        court.updateCourt(
            stadiumId = request.stadiumId,
            memberId = memberId,
            name = request.name,
            description = request.description,
            pricePerHour = request.pricePerHour
        )

        return CourtDetailResponse.from(court)
    }

    @Transactional
    override fun deleteCourt(request: CourtDeleteServiceRequest, member: Member, courtId: Long) {
        val memberId: Long = member.memberId ?: throw IllegalArgumentException("Member ID는 null일 수 없습니다.")

        validateStadiumOwnership(request.stadiumId, memberId)

        val court = findEntityByIdOrThrowException<Court>(
            repository = courtRepository,
            id = courtId,
            exceptionMessage = ExceptionMessage.COURT_NOT_FOUND
        )

        court.deleteCourt(request.stadiumId, memberId)
        courtRepository.delete(court)
    }

    private fun validateStadiumOwnership(stadiumId: Long, memberId: Long): Stadium {
        findEntityByIdOrThrowException<Member>(
            repository = memberRepository,
            id = memberId,
            exceptionMessage = ExceptionMessage.MEMBER_NOT_FOUND
        )

        return findEntityByIdOrThrowException<Stadium>(
            repository = stadiumRepository,
            id = stadiumId,
            exceptionMessage = ExceptionMessage.STADIUM_NOT_FOUND
        )
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