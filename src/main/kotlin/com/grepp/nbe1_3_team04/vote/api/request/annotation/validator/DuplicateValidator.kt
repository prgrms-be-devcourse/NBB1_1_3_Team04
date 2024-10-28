package com.grepp.nbe1_3_team04.vote.api.request.annotation.validator

import com.grepp.nbe1_3_team04.vote.api.request.annotation.Duplicate
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class DuplicateValidator : ConstraintValidator<Duplicate?, List<*>?> {
    override fun isValid(value: List<*>?, context: ConstraintValidatorContext?): Boolean {
        return value!!.distinct().size == value.size
    }
}
