package com.grepp.nbe1_3_team04.member.jwt.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenExpirationTime: Long
) {
    companion object {
        fun of(accessToken: String, refreshToken: String, refreshTokenExpirationTime: Long): TokenResponse {
            return TokenResponse(
                accessToken,
                refreshToken,
                refreshTokenExpirationTime)
        }
    }
}
