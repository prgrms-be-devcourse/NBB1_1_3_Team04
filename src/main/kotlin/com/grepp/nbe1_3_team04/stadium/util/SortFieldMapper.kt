package com.grepp.nbe1_3_team04.stadium.util

object SortFieldMapper {
    private val SORT_FIELD_MAP = mapOf(
        "COURT" to "courtId",
        "STADIUM" to "stadiumId",
        "NAME" to "name",
        "ADDRESS" to "address"
    )

    fun getDatabaseField(sortField: String): String = SORT_FIELD_MAP[sortField] ?: "name"
}