package com.kanzun.perbendaharaan.core.model

import java.text.NumberFormat
import java.util.Locale

data class Money(
    val amountInCents: Long,
    val currency: String = "IDR",
) {
    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        return Money(amountInCents + other.amountInCents, currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Currency mismatch: $currency vs ${other.currency}" }
        return Money(amountInCents - other.amountInCents, currency)
    }

    operator fun unaryMinus(): Money {
        return Money(-amountInCents, currency)
    }

    fun formatRupiah(): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp " + formatter.format(amountInCents)
    }

    val formatted: String
        get() = formatRupiah()


    companion object {
        val ZERO = Money(0L)
        fun of(amount: Long, currency: String = "IDR") = Money(amount, currency)
    }
}
