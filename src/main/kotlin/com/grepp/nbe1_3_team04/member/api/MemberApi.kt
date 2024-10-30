package com.grepp.nbe1_3_team04.member.api

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.member.api.request.JoinRequest
import com.grepp.nbe1_3_team04.member.api.request.LoginRequest
import com.grepp.nbe1_3_team04.member.api.request.UpdatePasswordRequest
import com.grepp.nbe1_3_team04.member.api.request.UpdateRequest
import com.grepp.nbe1_3_team04.member.jwt.JwtTokenUtil
import com.grepp.nbe1_3_team04.member.jwt.response.TokenResponse
import com.grepp.nbe1_3_team04.member.service.CookieService
import com.grepp.nbe1_3_team04.member.service.MemberService
import com.grepp.nbe1_3_team04.member.service.response.MemberResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team4.footwithme.member.jwt.PrincipalDetails

@RestController
@RequestMapping("/api/v1/members")
class MemberApi( private val memberService: MemberService,
                 private val cookieService: CookieService
    ) {

    @PostMapping("/join")
    fun join(@RequestBody request: @Valid JoinRequest): ApiResponse<MemberResponse> {
        return ApiResponse.created(memberService.join(request.toServiceRequest()))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: @Valid LoginRequest, response: HttpServletResponse): ApiResponse<TokenResponse> {
        val tokenResponse: TokenResponse = memberService.login(request.toServiceRequest())
        cookieService.setHeader(response, tokenResponse.refreshToken) // 쿠키에 refreshToken 저장

        return ApiResponse.ok(tokenResponse)
    }

    @DeleteMapping("/logout")
    fun logout(request: HttpServletRequest): ApiResponse<String> {
        return ApiResponse.ok(memberService.logout(request))
    }

    @PostMapping("/reissue")
    fun reissue(
        request: HttpServletRequest, response: HttpServletResponse,
        @RequestHeader(name = JwtTokenUtil.REFRESH_TOKEN) refreshToken: String?
    ): ApiResponse<TokenResponse> {
        val tokenResponse: TokenResponse = memberService.reissue(request, refreshToken)
        cookieService.setHeader(response, tokenResponse.refreshToken) // 쿠키에 refreshToken 저장

        return ApiResponse.ok(tokenResponse)
    }

    @PutMapping("/update")
    fun update(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: @Valid UpdateRequest
    ): ApiResponse<MemberResponse> {
        return ApiResponse.ok(memberService.update(principalDetails.member, request.toServiceRequest()))
    }

    @PutMapping("/update-password")
    fun updatePassword(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
        @RequestBody request: @Valid UpdatePasswordRequest
    ): ApiResponse<MemberResponse> {
        return ApiResponse.ok(memberService.updatePassword(principalDetails.member, request.toServiceRequest()))
    }
}
