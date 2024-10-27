package com.grepp.nbe1_3_team04.global.repository

interface CustomGlobalRepository<T> {
    fun findActiveById(id: Long): T?
}
