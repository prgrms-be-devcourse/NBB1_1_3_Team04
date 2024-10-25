package com.grepp.nbe1_3_team04.vote.api.request.annotation

import com.grepp.nbe1_3_team04.vote.api.request.annotation.validator.DuplicateValidator
import jakarta.validation.Constraint
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DuplicateValidator::class])
annotation class Duplicate(
    val message: String = "중복된 값이 포함되어 있습니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)
