package com.cuangx.finance.core.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {

    fun formatAmount(amount: Double, currencyCode: String = "IDR", locale: Locale = Locale.getDefault()): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(locale)
            formatter.currency = Currency.getInstance(currencyCode)
            formatter.format(amount)
        } catch (e: Exception) {
            // Fallback for unknown currencies
            "$currencyCode ${String.format("%,.2f", amount)}"
        }
    }

    fun formatIDR(amount: Double): String {
        return formatAmount(amount, "IDR", Locale("id", "ID"))
    }

    fun formatIDRCompact(amount: Double): String {
        return when {
            amount >= 1_000_000_000 -> String.format("Rp %.1fM", amount / 1_000_000_000)
            amount >= 1_000_000 -> String.format("Rp %.1fjt", amount / 1_000_000)
            amount >= 1_000 -> String.format("Rp %.1frb", amount / 1_000)
            else -> formatIDR(amount)
        }
    }

    fun formatPercent(value: Double): String {
        return String.format("%+.2f%%", value)
    }
}
