package com.grepp.nbe1_3_team04.reservation.domain


enum class GameStatus {
    PENDING,
    READY,
    IGNORE,
    PLAY,
    DONE;

    fun isReadyOrIgnore(): Boolean {
        return this == READY || this == IGNORE
    }
}
