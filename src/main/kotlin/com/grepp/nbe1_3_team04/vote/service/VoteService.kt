package com.grepp.nbe1_3_team04.vote.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.vote.domain.Vote
import com.grepp.nbe1_3_team04.vote.service.request.ChoiceCreateServiceRequest
import com.grepp.nbe1_3_team04.vote.service.request.VoteCourtCreateServiceRequest
import com.grepp.nbe1_3_team04.vote.service.request.VoteDateCreateServiceRequest
import com.grepp.nbe1_3_team04.vote.service.request.VoteUpdateServiceRequest
import com.grepp.nbe1_3_team04.vote.service.response.AllVoteResponse
import com.grepp.nbe1_3_team04.vote.service.response.VoteResponse
import org.springframework.stereotype.Component

@Component
interface VoteService {
    fun createCourtVote(request: VoteCourtCreateServiceRequest, teamId: Long, member: Member): VoteResponse

    fun createDateVote(request: VoteDateCreateServiceRequest, teamId: Long, member: Member): VoteResponse

    fun getVote(voteId: Long): VoteResponse

    fun deleteVote(voteId: Long, member: Member): Long

    fun createChoice(request: ChoiceCreateServiceRequest, voteId: Long, member: Member): VoteResponse

    fun deleteChoice(voteId: Long, member: Member): VoteResponse

    fun updateVote(serviceRequest: VoteUpdateServiceRequest, voteId: Long, member: Member): VoteResponse

    fun closeVote(voteId: Long, member: Member): VoteResponse

    fun getAllVotes(teamId: Long): List<AllVoteResponse>

    fun makeReservation(vote: Vote)
}
