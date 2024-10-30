package com.grepp.nbe1_3_team04.member.service

import com.grepp.nbe1_3_team04.member.domain.Member
import com.grepp.nbe1_3_team04.member.jwt.response.TokenResponse
import com.grepp.nbe1_3_team04.member.service.request.JoinServiceRequest
import com.grepp.nbe1_3_team04.member.service.request.LoginServiceRequest
import com.grepp.nbe1_3_team04.member.service.request.UpdatePasswordServiceRequest
import com.grepp.nbe1_3_team04.member.service.request.UpdateServiceRequest
import com.grepp.nbe1_3_team04.member.service.response.MemberResponse
import jakarta.servlet.http.HttpServletRequest

interface MemberService {
    fun join(serviceRequest: JoinServiceRequest): MemberResponse

    fun login(serviceRequest: LoginServiceRequest): TokenResponse

    fun logout(request: HttpServletRequest): String

    fun reissue(request: HttpServletRequest, refreshToken: String?): TokenResponse

    fun update(member: Member, request: UpdateServiceRequest): MemberResponse

    fun updatePassword(member: Member, serviceRequest: UpdatePasswordServiceRequest): MemberResponse
}
