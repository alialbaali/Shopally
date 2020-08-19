package com.shopping.domain.model.valueObject

inline class Balance(val value: Double) {

    companion object {

        private const val ERROR = "Amount is invalid"

        val MIN = Balance(0.0)

        val MAX = Balance(10_000.0)

        fun create(amount: Double): Result<Balance> =
            if (amount >= MIN.value && amount <= MAX.value)
                Result.success(Balance(amount))
            else
                Result.failure(Throwable(ERROR))

    }

    operator fun compareTo(balance: Balance): Int =
        when {
            this.value == balance.value -> 0
            this.value >= balance.value -> 1
            else -> -1
        }

    operator fun plus(balance: Balance): Balance =
        if (this.value.plus(balance.value) <= MAX.value)
            Balance(this.value.plus(balance.value))
        else
            this

    operator fun minus(balance: Balance): Balance =
        if (this.value >= balance.value)
            Balance(this.value.minus(balance.value))
        else
            this
}