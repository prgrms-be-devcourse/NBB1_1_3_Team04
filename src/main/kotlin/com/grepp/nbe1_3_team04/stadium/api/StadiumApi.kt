package com.grepp.nbe1_3_team04.stadium.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.stadium.api.request.StadiumSearchByLocationRequest
import com.grepp.nbe1_3_team04.stadium.api.request.validation.StadiumAllowedValues
import com.grepp.nbe1_3_team04.stadium.service.StadiumService
import com.grepp.nbe1_3_team04.stadium.service.response.StadiumDetailResponse
import com.grepp.nbe1_3_team04.stadium.service.response.StadiumsResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Slice
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stadium")
class StadiumApi(
    private val stadiumService: StadiumService
) {

    companion object {
        private val log = LoggerFactory.getLogger(StadiumApi::class.java)
    }

    @GetMapping("/stadiums")
    fun stadiums(
        @RequestParam(defaultValue = "0") page: Int = 0,
        @RequestParam(defaultValue = "STADIUM") @StadiumAllowedValues sort: String = "STADIUM"
    ): ApiResponse<Slice<StadiumsResponse>> {
        val stadiumList = stadiumService.getStadiumList(page, sort)
        return ApiResponse.ok(stadiumList)
    }

    @GetMapping("/stadiums/{stadiumId}/detail")
    fun getStadiumDetailById(@PathVariable stadiumId: Long): ApiResponse<StadiumDetailResponse> {
        val stadiumDetailResponse = stadiumService.getStadiumDetail(stadiumId)
        return ApiResponse.ok(stadiumDetailResponse)
    }

    @GetMapping("/stadiums/search/name")
    fun getStadiumsByName(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int = 0,
        @RequestParam(defaultValue = "STADIUM") @StadiumAllowedValues sort: String = "STADIUM"
    ): ApiResponse<Slice<StadiumsResponse>> {
        val stadiums = stadiumService.getStadiumsByName(query, page, sort)
        return ApiResponse.ok(stadiums)
    }

    @GetMapping("/stadiums/search/address")
    fun getStadiumsByAddress(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int = 0,
        @RequestParam(defaultValue = "STADIUM") @StadiumAllowedValues sort: String = "STADIUM"
    ): ApiResponse<Slice<StadiumsResponse>> {
        val stadiums = stadiumService.getStadiumsByAddress(query, page, sort)
        return ApiResponse.ok(stadiums)
    }

    @PostMapping("/stadiums/search/location")
    fun getStadiumsByLocation(
        @Validated @RequestBody request: StadiumSearchByLocationRequest,
        @RequestParam(defaultValue = "0") page: Int = 0,
        @RequestParam(defaultValue = "STADIUM") @StadiumAllowedValues sort: String = "STADIUM"
    ): ApiResponse<Slice<StadiumsResponse>> {
        val stadiums = stadiumService.getStadiumsWithinDistance(request.toServiceRequest(), page, sort)
        return ApiResponse.ok(stadiums)
    }
}