package com.shopping.domain.dto.customer.request

import com.shopping.Errors
import com.shopping.badRequestError
import java.time.LocalDate
import java.time.Year

private const val DefaultMonthOfDay = 1

class CreateCardRequest(
    val number: String?,
    val expMonth: Int?,
    val expYear: Int?,
    val cvc: Int?,
) {

    operator fun component1(): Long = number?.let {
        number.toLongOrNull() ?: badRequestError(Errors.InvalidCardNumber)
    } ?: badRequestError("Card number ${Errors.PropertyMissing}")

    operator fun component2(): LocalDate {

        val month = expMonth?.let {
            expMonth.takeIf { expMonth in 1..12 } ?: badRequestError(Errors.MonthProperty)
        } ?: badRequestError("exp_month ${Errors.PropertyMissing}")

        val year = expYear?.let {
            expYear.takeIf { expYear in Year.MIN_VALUE..Year.MAX_VALUE } ?: badRequestError(Errors.YearProperty)
        } ?: badRequestError("exp_year ${Errors.PropertyMissing}")

        return runCatching { LocalDate.of(year, month, DefaultMonthOfDay) }
            .getOrElse { badRequestError(Errors.InvalidDate) }
            .takeIf { it.isAfter(LocalDate.now()) } ?: badRequestError(Errors.CardDateInTheFuture)
    }

    operator fun component3(): Long = cvc?.toLong() ?: badRequestError("cvc ${Errors.PropertyMissing}")
}
