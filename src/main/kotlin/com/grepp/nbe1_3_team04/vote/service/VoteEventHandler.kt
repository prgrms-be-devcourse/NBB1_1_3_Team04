package com.grepp.nbe1_3_team04.vote.service

import com.grepp.nbe1_3_team04.vote.domain.Vote
import com.grepp.nbe1_3_team04.vote.domain.VoteItem
import com.grepp.nbe1_3_team04.vote.domain.VoteItemDate
import com.grepp.nbe1_3_team04.vote.repository.VoteRepository
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class VoteEventHandler(
    private val voteRepository: VoteRepository,
    private val voteService: VoteService
) {

    @Async
    @Transactional
    @EventListener
    fun onClosedVote(event: RegisteredVoteEvent) {
        val vote: Vote = voteRepository.findNotDeletedVoteById(event.voteId)
            ?: throw IllegalArgumentException("해당하는 투표가 없습니다.")

        // 만약 이 투표가 장소 투표라면 아무일도 일어나지 않는다.
        val voteItems: List<VoteItem> = vote.voteItems
        if (voteItems[0] is VoteItemDate) {
            voteService.makeReservation(vote)
            vote.updateVoteStatusToClose()
        }
    }

}
