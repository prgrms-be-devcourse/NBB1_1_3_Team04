package com.grepp.nbe1_3_team04.vote.api.request

import com.grepp.nbe1_3_team04.vote.service.request.VoteUpdateServiceRequest
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class VoteUpdateRequest(
    @field:NotBlank(message = "제목은 필수입니다.")
    @field:Size(max = 50, message = "제목은 50자 이하여야 합니다.")
    val title: String?,
    @field:Future(message = "투표 종료 시간은 현재 시간보다 미래의 시간으로 지정해야합니다.")
    val endAt: LocalDateTime?
) {
    fun toServiceRequest(): VoteUpdateServiceRequest {
        return VoteUpdateServiceRequest(title!!, endAt!!)
    }
}
