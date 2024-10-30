package com.grepp.nbe1_3_team04.vote.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.stadium.repository.CourtRepository
import com.grepp.nbe1_3_team04.team.repository.TeamRepository
import com.grepp.nbe1_3_team04.vote.domain.*
import com.grepp.nbe1_3_team04.vote.repository.ChoiceRepository
import com.grepp.nbe1_3_team04.vote.repository.VoteItemRepository
import com.grepp.nbe1_3_team04.vote.repository.VoteRepository
import com.grepp.nbe1_3_team04.vote.service.request.ChoiceCreateServiceRequest
import com.grepp.nbe1_3_team04.vote.service.request.VoteCourtCreateServiceRequest
import com.grepp.nbe1_3_team04.vote.service.request.VoteDateCreateServiceRequest
import com.grepp.nbe1_3_team04.vote.service.request.VoteUpdateServiceRequest
import com.grepp.nbe1_3_team04.vote.service.response.AllVoteResponse
import com.grepp.nbe1_3_team04.vote.service.response.VoteItemResponse
import com.grepp.nbe1_3_team04.vote.service.response.VoteResponse
import jakarta.annotation.PostConstruct
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

@Service
class VoteServiceImpl(
    private val voteRepository: VoteRepository,
    private val voteItemRepository: VoteItemRepository,
    private val courtRepository: CourtRepository,
    private val teamRepository: TeamRepository,
    private val choiceRepository: ChoiceRepository,
    private val taskScheduler: TaskScheduler,
    private val eventPublisher: ApplicationEventPublisher
) :
    VoteService {

    private val scheduledTasks: MutableMap<Long, ScheduledFuture<*>> = ConcurrentHashMap<Long, ScheduledFuture<*>>()

    @PostConstruct
    private fun initializeScheduledTasks() {
        val activeVotes: List<Vote> = voteRepository.findOpenedVotes()
        for (vote in activeVotes) {
            addVoteTaskToTaskSchedule(vote)
        }
    }

    @Transactional
    override fun createCourtVote(request: VoteCourtCreateServiceRequest, teamId: Long, member: Member): VoteResponse {
        val memberId: Long = getMemberIdFrom(member)
        validateTeamByTeamId(teamId)

        val vote: Vote = Vote.create(memberId, teamId, request.title, request.endAt)
        val savedVote: Vote = voteRepository.save(vote)
        addVoteTaskToTaskSchedule(savedVote)

        val voteItemLocates: List<VoteItemLocate> = createVoteItemLocate(request, savedVote)

        val savedVoteItems: List<VoteItemLocate> = voteItemRepository.saveAll(voteItemLocates)
        val voteItemResponses: List<VoteItemResponse> = convertVoteItemsToResponseFrom(savedVoteItems)
        return VoteResponse.of(vote, voteItemResponses)
    }

    @Transactional
    override fun createDateVote(request: VoteDateCreateServiceRequest, teamId: Long, member: Member): VoteResponse {
        val memberId: Long = getMemberIdFrom(member)
        validateTeamByTeamId(teamId)

        val vote: Vote = Vote.create(memberId, teamId, request.title, request.endAt)
        val savedVote: Vote = voteRepository.save(vote)
        addVoteTaskToTaskSchedule(savedVote)

        val savedVoteItems: List<VoteItemDate> = createVoteItemDate(request, savedVote)

        val voteItemResponses: List<VoteItemResponse> = convertVoteItemsToResponseFrom(savedVoteItems)
        return VoteResponse.of(savedVote, voteItemResponses)
    }

    override fun getVote(voteId: Long): VoteResponse {
        val vote: Vote = getVoteByVoteId(voteId)
        val voteItemResponses: List<VoteItemResponse> = convertVoteItemsToResponseFrom(vote.voteItems)
        return VoteResponse.of(vote, voteItemResponses)
    }

    @Transactional
    override fun deleteVote(voteId: Long, member: Member): Long {
        val vote: Vote = getVoteByVoteId(voteId)
        vote.checkWriterFromMemberId(getMemberIdFrom(member))
        cancelTaskInSchedulerFromVoteId(voteId)
        voteRepository.delete(vote)
        return voteId
    }

    @Transactional
    override fun createChoice(request: ChoiceCreateServiceRequest, voteId: Long, member: Member): VoteResponse {
        val memberId: Long = getMemberIdFrom(member)
        val vote: Vote = getVoteByVoteId(voteId)

        val choices: List<Choice> = createChoice(request, memberId)
        choiceRepository.saveAll(choices)

        val voteItemResponses: List<VoteItemResponse> = convertVoteItemsToResponseFrom(vote.voteItems)
        return VoteResponse.of(vote, voteItemResponses)
    }

    @Transactional
    override fun deleteChoice(voteId: Long, member: Member): VoteResponse {
        val memberId: Long = getMemberIdFrom(member)
        val vote: Vote = getVoteByVoteId(voteId)

        val choices: List<Choice> = choiceRepository.findByMemberIdAndVoteId(memberId, voteId)

        choiceRepository.deleteAllInBatch(choices)
        val voteItemResponses: List<VoteItemResponse> = convertVoteItemsToResponseFrom(vote.voteItems)
        return VoteResponse.of(vote, voteItemResponses)
    }

    @Transactional
    override fun updateVote(serviceRequest: VoteUpdateServiceRequest, voteId: Long, member: Member): VoteResponse {
        val memberId: Long = getMemberIdFrom(member)
        val vote: Vote = getVoteByVoteId(voteId)

        vote.update(serviceRequest.title, serviceRequest.endAt, memberId)
        cancelTaskInSchedulerFromVoteId(voteId)
        addVoteTaskToTaskSchedule(vote)

        val voteItemResponses: List<VoteItemResponse> = convertVoteItemsToResponseFrom(vote.voteItems)
        return VoteResponse.of(vote, voteItemResponses)
    }

    @Transactional
    override fun closeVote(voteId: Long, member: Member): VoteResponse {
        val vote: Vote = getVoteByVoteId(voteId)
        vote.checkWriterFromMemberId(getMemberIdFrom(member))
        if (vote.voteItems[0] is VoteItemDate) {
            makeReservation(vote)
        }
        vote.updateVoteStatusToClose()
        cancelTaskInSchedulerFromVoteId(voteId)
        return VoteResponse.of(vote, convertVoteItemsToResponseFrom(vote.voteItems))
    }

    override fun getAllVotes(teamId: Long): List<AllVoteResponse> {
        validateTeamByTeamId(teamId)
        val votes: List<Vote> = voteRepository.findAllByTeamId(teamId)
        return votes.map(AllVoteResponse::from)
    }

    private fun createVoteItemDate(request: VoteDateCreateServiceRequest, savedVote: Vote): List<VoteItemDate> {
        return voteItemRepository.saveAll(request.choices
            .map { choice -> VoteItemDate.create(savedVote, choice) })
    }

    private fun createVoteItemLocate(request: VoteCourtCreateServiceRequest, savedVote: Vote): List<VoteItemLocate> {
        checkDuplicateStadiumIds(request.courtIds)
        return request.courtIds
            .map { stadiumId -> VoteItemLocate.create(savedVote, stadiumId) }
    }

    private fun checkDuplicateStadiumIds(requestStadiumIds: List<Long>) {
        require(courtRepository.countCourtByCourtIds(requestStadiumIds) == requestStadiumIds.size.toLong()) { "존재하지 않는 구장이 포함되어 있습니다." }
    }

    private fun createChoice(request: ChoiceCreateServiceRequest, memberId: Long): List<Choice> {
        return request.voteItemIds
            .map { voteItemId -> Choice.create(memberId, voteItemId) }
    }

    private fun getVoteByVoteId(voteId: Long): Vote {
        return voteRepository.findNotDeletedVoteById(voteId)
            ?: throw IllegalArgumentException("존재하지 않는 투표입니다.")
    }

    private fun validateTeamByTeamId(teamId: Long) {
        require(teamRepository.existsById(teamId)) { "존재하지 않는 팀입니다." }
    }

    private fun <T : VoteItem?> convertVoteItemsToResponseFrom(voteItems: List<T>): List<VoteItemResponse> {
        return voteItems.map { voteItem: T -> this.convertVoteItemToResponse(voteItem) }
    }

    private fun <T : VoteItem?> convertVoteItemToResponse(voteItem: T): VoteItemResponse {
        if (voteItem is VoteItemLocate) {
            return convertToVoteItemResponseFrom(voteItem)
        }
        if (voteItem is VoteItemDate) {
            return convertToVoteItemResponseFrom(voteItem)
        }
        throw IllegalArgumentException("지원하지 않는 투표 항목입니다.")
    }


    private fun convertToVoteItemResponseFrom(voteItemDate: VoteItemDate): VoteItemResponse {
        return VoteItemResponse.of(
            voteItemDate.voteItemId!!,
            voteItemDate.time.toString(),
            choiceRepository.findMemberIdsByVoteItemId(voteItemDate.voteItemId!!)
        )
    }

    private fun convertToVoteItemResponseFrom(voteItemLocate: VoteItemLocate): VoteItemResponse {
        return VoteItemResponse.of(
            voteItemLocate.voteItemId!!,
            courtRepository.findCourtNameByCourtId(voteItemLocate.courtId)
                ?: throw IllegalArgumentException("존재하지 않는 구장입니다."),
            choiceRepository.findMemberIdsByVoteItemId(voteItemLocate.voteItemId!!)
        )
    }

    private fun getMemberIdFrom(member: Member): Long {
        val memberId: Long = member.memberId ?: throw IllegalArgumentException("로그인이 필요합니다.")
        return memberId
    }

    private fun addVoteTaskToTaskSchedule(vote: Vote) {
        val voteId = vote.voteId ?: throw IllegalArgumentException("존재하지 않는 투표입니다.")
        val scheduledTask: ScheduledFuture<*> = taskScheduler.schedule(publishClosedVoteTask(voteId), vote.instantEndAt)
        scheduledTasks[voteId] = scheduledTask
    }

    private fun publishClosedVoteTask(voteId: Long): Runnable {
        return Runnable { eventPublisher.publishEvent(RegisteredVoteEvent(voteId)) }
    }

    private fun cancelTaskInSchedulerFromVoteId(voteId: Long) {
        val scheduledTask = scheduledTasks[voteId] ?: throw IllegalArgumentException("해당하는 투표 ID로 등록된 작업이 없습니다.")
        scheduledTask.cancel(false)
        scheduledTasks.remove(voteId)
    }

    override fun makeReservation(vote: Vote) {
        val voteItemDateId: Long =
            choiceRepository.maxChoiceCountByVoteId(vote.voteId!!) ?: throw IllegalArgumentException("해당하는 일정이 없습니다.")
        val memberIds: List<Long> = choiceRepository.findMemberIdsByVoteItemId(voteItemDateId)
        val voteItemDate: VoteItemDate = getVoteItemDate(vote, voteItemDateId)
        val memberId: Long = vote.memberId
        val matchDate: LocalDateTime = voteItemDate.time
        val teamId: Long = vote.teamId

        val voteItemLocate: VoteItemLocate = getVoteItemLocate(teamId)
        val courtId: Long = voteItemLocate.courtId

        publishEndVoteEvent(courtId, memberId, teamId, matchDate, memberIds)
    }

    private fun getVoteItemDate(vote: Vote, voteItemDateId: Long): VoteItemDate {
        return vote.voteItems
            .filter { voteItem -> voteItem.voteItemId == voteItemDateId }
            .firstNotNullOfOrNull { it as? VoteItemDate } ?: throw IllegalArgumentException("해당하는 일정이 없습니다.")
    }

    private fun getVoteItemLocate(teamId: Long): VoteItemLocate {
        val locateVote: Vote =
            voteRepository.findRecentlyVoteByTeamId(teamId) ?: throw IllegalArgumentException("해당하는 팀의 투표가 없습니다.")
        val voteItemLocateId: Long = choiceRepository.maxChoiceCountByVoteId(locateVote.voteId!!)
            ?: throw IllegalArgumentException("해당하는 구장이 없습니다.")
        return locateVote.voteItems
            .filter { voteItem -> voteItem.voteItemId == voteItemLocateId }
            .firstNotNullOfOrNull { it as? VoteItemLocate } ?: throw IllegalArgumentException("해당하는 구장 아이디가 없습니다.")
    }

    private fun publishEndVoteEvent(
        courtId: Long,
        memberId: Long,
        teamId: Long,
        matchDate: LocalDateTime,
        memberIds: List<Long>
    ) {
        eventPublisher.publishEvent(EndVoteEvent(courtId, memberId, teamId, matchDate, memberIds))
    }
}
