package com.cuangx.finance.core.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    private val idrLocale = Locale("id", "ID")

    fun formatIDR(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(idrLocale)
        return formatter.format(amount)
    }

    fun formatIDRCompact(amount: Double): String {
        return when {
            amount >= 1_000_000_000 -> String.format("Rp %.1fM", amount / 1_000_000)
            amount >= 1_000_000 -> String.format("Rp %.1fjt", amount / 1_000_000)
            amount >= 1_000 -> String.format("Rp %.1frb", amount / 1_000)
            else -> formatIDR(amount)
        }
    }

    fun formatPercent(value: Double): String {
        return String.format("%+.2f%%", value)
    }

    fun formatAmount(amount: Double, currency: String = "IDR"): String {
        return when (currency) {
            "IDR" -> formatIDR(amount)
            "USD" -> "$${String.format("%,.2f", amount)}"
            else -> "$currency ${String.format("%,.2f", amount)}"
        }
    }
}
