package com.grepp.nbe1_3_team04.global.exception

import com.grepp.nbe1_3_team04.global.api.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException::class)
    protected fun bindException(e: BindException): ApiResponse<Any?> {
        val message = e.bindingResult.allErrors.firstOrNull()?.defaultMessage ?: "Invalid request"
        return ApiResponse.of(
            HttpStatus.BAD_REQUEST,
            message,
            null
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class)
    protected fun illegalArgumentException(e: IllegalArgumentException): ApiResponse<Any?> {
        val message = e.message ?: "Invalid argument"
        return ApiResponse.of(
            HttpStatus.BAD_REQUEST,
            message,
            null
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleHandlerMethodValidationException(e: HandlerMethodValidationException): ApiResponse<Any?> {
        val message = e.allErrors.joinToString(", ") { it.defaultMessage ?: "Validation error" }
        return ApiResponse.of(
            HttpStatus.BAD_REQUEST,
            message,
            null
        )
    }
}
