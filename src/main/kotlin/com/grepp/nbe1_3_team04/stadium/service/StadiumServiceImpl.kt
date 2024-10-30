package com.grepp.nbe1_3_team04.stadium.service

import com.grepp.nbe1_3_team04.global.exception.ExceptionMessage
import com.grepp.nbe1_3_team04.global.repository.CustomGlobalRepository
import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.repository.MemberRepository
import com.grepp.nbe1_3_team04.stadium.domain.Court
import com.grepp.nbe1_3_team04.stadium.domain.Stadium
import com.grepp.nbe1_3_team04.stadium.repository.CourtRepository
import com.grepp.nbe1_3_team04.stadium.repository.StadiumRepository
import com.grepp.nbe1_3_team04.stadium.service.request.StadiumRegisterServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.request.StadiumSearchByLocationServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.request.StadiumUpdateServiceRequest
import com.grepp.nbe1_3_team04.stadium.service.response.StadiumDetailResponse
import com.grepp.nbe1_3_team04.stadium.service.response.StadiumsResponse
import com.grepp.nbe1_3_team04.stadium.util.SortFieldMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StadiumServiceImpl(
    private val stadiumRepository: StadiumRepository,
    private val memberRepository: MemberRepository,
    private val courtRepository: CourtRepository
) : StadiumService {

    companion object {
        private val log = LoggerFactory.getLogger(StadiumServiceImpl::class.java)
    }

    override fun getStadiumList(page: Int, sort: String): Slice<StadiumsResponse> {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by(SortFieldMapper.getDatabaseField(sort)))
        return stadiumRepository.findAllActiveStadiums(pageable).map(StadiumsResponse::from)
    }

    override fun getStadiumDetail(id: Long): StadiumDetailResponse {
        val stadium = findEntityByIdOrThrowException<Stadium>(
            repository = stadiumRepository,
            id = id,
            exceptionMessage = ExceptionMessage.STADIUM_NOT_FOUND
        )
        return StadiumDetailResponse.from(stadium)
    }

    override fun getStadiumsByName(query: String, page: Int, sort: String): Slice<StadiumsResponse> {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by(SortFieldMapper.getDatabaseField(sort)))
        return stadiumRepository.findByNameContainingIgnoreCase(query, pageable).map(StadiumsResponse::from)
    }

    override fun getStadiumsByAddress(address: String, page: Int, sort: String): Slice<StadiumsResponse> {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by(SortFieldMapper.getDatabaseField(sort)))
        return stadiumRepository.findByAddressContainingIgnoreCase(address, pageable).map(StadiumsResponse::from)
    }

    override fun getStadiumsWithinDistance(
        request: StadiumSearchByLocationServiceRequest,
        page: Int,
        sort: String
    ): Slice<StadiumsResponse> {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by(SortFieldMapper.getDatabaseField(sort)))
        return stadiumRepository.findStadiumsByLocation(
            latitude = request.latitude,
            longitude = request.longitude,
            distance = request.distance,
            pageable = pageable
        ).map(StadiumsResponse::from)
    }

    @Transactional
    override fun registerStadium(request: StadiumRegisterServiceRequest, member: Member): StadiumDetailResponse {
        val stadium = Stadium.create(
            member = member,
            name = request.name,
            address = request.address,
            phoneNumber = request.phoneNumber,
            description = request.description,
            latitude = request.latitude,
            longitude = request.longitude
        )
        stadiumRepository.save(stadium)
        return StadiumDetailResponse.from(stadium)
    }

    @Transactional
    override fun updateStadium(
        request: StadiumUpdateServiceRequest,
        member: Member,
        stadiumId: Long
    ): StadiumDetailResponse {
        val memberId: Long = member.memberId ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_ABNORMAL.text)

        val stadium = validateStadiumOwnership(memberId, stadiumId)

        stadium.updateStadium(
            memberId = memberId,
            name = request.name,
            address = request.address,
            phoneNumber = request.phoneNumber,
            description = request.description,
            latitude = request.latitude,
            longitude = request.longitude
        )

        return StadiumDetailResponse.from(stadium)
    }

    @Transactional
    override fun deleteStadium(member: Member, stadiumId: Long) {
        val memberId: Long = member.memberId ?: throw IllegalArgumentException(ExceptionMessage.MEMBER_ABNORMAL.text)

        val stadium = validateStadiumOwnership(memberId, stadiumId)

        stadium.deleteStadium(memberId)

        val courts: List<Court> = courtRepository.findActiveByStadiumId(stadiumId)
        if (courts.isNotEmpty()) {
            courtRepository.deleteAll(courts)
        }

        stadiumRepository.delete(stadium)
    }

    private fun validateStadiumOwnership(memberId: Long, stadiumId: Long): Stadium {
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