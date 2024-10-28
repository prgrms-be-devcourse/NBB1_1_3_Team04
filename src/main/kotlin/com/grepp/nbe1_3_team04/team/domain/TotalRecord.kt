package com.grepp.nbe1_3_team04.team.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class TotalRecord{
    @Column(nullable = false)
    var winCount: Int = 0
        protected set

    @Column(nullable = false)
    var drawCount: Int = 0
        protected set

    @Column(nullable = false)
    var loseCount: Int = 0
        protected set
}