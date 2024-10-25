package com.grepp.nbe1_3_team04.vote.api.request

import com.grepp.nbe1_3_team04.vote.api.request.annotation.Duplicate
import com.grepp.nbe1_3_team04.vote.service.request.VoteCourtCreateServiceRequest
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class VoteCourtCreateRequest(
    @field:NotBlank(message = "제목은 필수입니다.")
    @field:Size(max = 50, message = "제목은 50자 이하여야 합니다.")
    val title: String?,
    @field:Future(message = "투표 종료 시간은 현재 시간보다 미래의 시간으로 지정해야합니다.")
    val endAt: LocalDateTime?,
    @field:Duplicate(message = "중복된 구장은 포함 할 수 없습니다.") @param:Duplicate(message = "중복된 구장은 포함 할 수 없습니다.")
    @field:Size(min = 1, message = "구장 선택은 필수입니다.")
    val choices: MutableList<CourtChoices>?
) {
    fun toServiceRequest(): VoteCourtCreateServiceRequest {
        return VoteCourtCreateServiceRequest(
            title!!,
            endAt!!,
            choices!!.stream()
                .map<Long>(CourtChoices::courtId)
                .toList()
        )
    }
}
