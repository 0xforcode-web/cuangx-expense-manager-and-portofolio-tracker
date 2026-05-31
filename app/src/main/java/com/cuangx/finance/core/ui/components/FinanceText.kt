package com.cuangx.finance.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.cuangx.finance.core.ui.theme.ExpenseColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.ui.theme.LossColor
import com.cuangx.finance.core.ui.theme.NeutralAmountColor
import com.cuangx.finance.core.ui.theme.ProfitColor
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.domain.model.TransactionType

fun signedTransactionAmount(type: TransactionType, amount: Double): String {
    val prefix = when (type) {
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE -> "-"
        TransactionType.TRANSFER -> ""
    }
    return "$prefix${CurrencyFormatter.formatIDR(amount)}"
}

fun transactionAmountColor(type: TransactionType): Color {
    return when (type) {
        TransactionType.INCOME -> IncomeColor
        TransactionType.EXPENSE -> ExpenseColor
        TransactionType.TRANSFER -> NeutralAmountColor
    }
}

@Composable
fun MoneyText(
    amount: Double,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    emphasized: Boolean = false
) {
    Text(
        text = CurrencyFormatter.formatIDR(amount),
        modifier = modifier,
        style = if (emphasized) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.titleMedium,
        fontWeight = if (emphasized) FontWeight.ExtraBold else FontWeight.SemiBold,
        color = color
    )
}

@Composable
fun DeltaText(
    amount: Double,
    modifier: Modifier = Modifier,
    percent: Double? = null
) {
    val positive = amount >= 0
    val text = buildString {
        append(if (positive) "+" else "")
        append(CurrencyFormatter.formatIDR(amount))
        if (percent != null) {
            append(" (")
            append(CurrencyFormatter.formatPercent(percent))
            append(")")
        }
    }
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = if (positive) ProfitColor else LossColor
    )
}
