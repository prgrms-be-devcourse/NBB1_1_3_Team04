package com.grepp.nbe1_3_team04.stadium.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.stadium.api.request.validation.CourtAllowedValues
import com.grepp.nbe1_3_team04.stadium.service.CourtService
import com.grepp.nbe1_3_team04.stadium.service.response.CourtDetailResponse
import com.grepp.nbe1_3_team04.stadium.service.response.CourtsResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Slice
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/court")
class CourtApi(
    private val courtService: CourtService
) {

    companion object {
        private val log = LoggerFactory.getLogger(CourtApi::class.java)
    }

    @GetMapping("/")
    fun getAllCourts(
        @RequestParam(defaultValue = "0") page: Int = 0,
        @RequestParam(defaultValue = "COURT") @CourtAllowedValues sort: String = "COURT"
    ): ApiResponse<Slice<CourtsResponse>> {
        val courts = courtService.getAllCourts(page, sort)
        return ApiResponse.ok(courts)
    }

    @GetMapping("/{stadiumId}/courts")
    fun getCourtsByStadiumId(
        @PathVariable stadiumId: Long,
        @RequestParam(defaultValue = "0") page: Int = 0,
        @RequestParam(defaultValue = "COURT") @CourtAllowedValues sort: String = "COURT"
    ): ApiResponse<Slice<CourtsResponse>> {
        val courts = courtService.getCourtsByStadiumId(stadiumId, page, sort)
        return ApiResponse.ok(courts)
    }

    @GetMapping("/{courtId}/detail")
    fun getCourtDetailById(@PathVariable courtId: Long): ApiResponse<CourtDetailResponse> {
        val court = courtService.getCourtByCourtId(courtId)
        return ApiResponse.ok(court)
    }
}