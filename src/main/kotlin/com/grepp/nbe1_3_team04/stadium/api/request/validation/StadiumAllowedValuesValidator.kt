package com.grepp.nbe1_3_team04.stadium.api.request.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class StadiumAllowedValuesValidator : ConstraintValidator<StadiumAllowedValues, String> {

    companion object {
        private val VALID_SORT_FIELDS = listOf("STADIUM", "NAME", "ADDRESS")
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) {
            return true
        }

        return if (VALID_SORT_FIELDS.contains(value)) {
            true
        } else {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "정렬 기준은 다음과 같습니다: $VALID_SORT_FIELDS"
            ).addConstraintViolation()
            false
        }
    }
}