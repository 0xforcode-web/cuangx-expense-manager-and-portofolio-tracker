package com.cuangx.finance.feature.expense.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuangx.finance.core.ui.theme.CategoryColors
import com.cuangx.finance.core.ui.theme.ExpenseColor
import com.cuangx.finance.core.ui.theme.IncomeColor
import com.cuangx.finance.core.util.CurrencyFormatter
import com.cuangx.finance.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCategories = if (uiState.selectedType == TransactionType.EXPENSE) {
        uiState.categoryExpenses
    } else {
        uiState.categoryIncome
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            // Income vs Expense Bar Chart
            item {
                IncomeExpenseBarChart(
                    totalIncome = uiState.totalIncome,
                    totalExpense = uiState.totalExpense
                )
            }

            // Balance Card
            item {
                BalanceCard(
                    income = uiState.totalIncome,
                    expense = uiState.totalExpense
                )
            }

            item {
                StatisticsTypeSelector(
                    selectedType = uiState.selectedType,
                    onSelectedTypeChange = viewModel::updateSelectedType
                )
            }

            item {
                if (selectedCategories.isEmpty()) {
                    Text(
                        text = "Belum ada data kategori untuk periode ini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    CategoryPieChart(
                        title = if (uiState.selectedType == TransactionType.EXPENSE) {
                            "Expense by Category"
                        } else {
                            "Income by Category"
                        },
                        categories = selectedCategories
                    )
                }
            }

            if (selectedCategories.isNotEmpty()) {
                item {
                    Text(
                        text = if (uiState.selectedType == TransactionType.EXPENSE) {
                            "Expense by Category"
                        } else {
                            "Income by Category"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(selectedCategories) { categoryExpense ->
                CategoryExpenseItem(categoryExpense = categoryExpense)
            }
        }
    }
}

@Composable
private fun StatisticsTypeSelector(
    selectedType: TransactionType,
    onSelectedTypeChange: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedType == TransactionType.EXPENSE,
            onClick = { onSelectedTypeChange(TransactionType.EXPENSE) },
            label = { Text("Expense") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ExpenseColor.copy(alpha = 0.18f)
            )
        )
        FilterChip(
            selected = selectedType == TransactionType.INCOME,
            onClick = { onSelectedTypeChange(TransactionType.INCOME) },
            label = { Text("Income") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = IncomeColor.copy(alpha = 0.18f)
            )
        )
    }
}

@Composable
private fun IncomeExpenseBarChart(totalIncome: Double, totalExpense: Double) {
    val maxValue = maxOf(totalIncome, totalExpense, 1.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Income vs Expense",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Income Bar
            Text(
                text = "Income",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (totalIncome / maxValue).toFloat())
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(IncomeColor)
                )
                Text(
                    text = CurrencyFormatter.formatIDR(totalIncome),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expense Bar
            Text(
                text = "Expense",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = (totalExpense / maxValue).toFloat())
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ExpenseColor)
                )
                Text(
                    text = CurrencyFormatter.formatIDR(totalExpense),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun BalanceCard(income: Double, expense: Double) {
    val balance = income - expense
    val isPositive = balance >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPositive) IncomeColor.copy(alpha = 0.1f) else ExpenseColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Balance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${if (isPositive) "+" else ""}${CurrencyFormatter.formatIDR(balance)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPositive) IncomeColor else ExpenseColor
            )
        }
    }
}

@Composable
private fun CategoryPieChart(
    title: String,
    categories: List<CategoryExpense>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                var startAngle = -90f
                categories.forEachIndexed { index, category ->
                    val sweep = (category.percentage / 100.0 * 360.0).toFloat()
                    drawArc(
                        color = if (category.category.id == -1L) {
                            Color(category.category.color)
                        } else {
                            CategoryColors[index % CategoryColors.size]
                        },
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = true
                    )
                    startAngle += sweep
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            categories.take(5).forEachIndexed { index, category ->
                val color = if (category.category.id == -1L) {
                    Color(category.category.color)
                } else {
                    CategoryColors[index % CategoryColors.size]
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = category.category.name,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${String.format("%.0f", category.percentage)}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryExpenseItem(categoryExpense: CategoryExpense) {
    val color = if (categoryExpense.category.id == -1L) {
        Color(categoryExpense.category.color)
    } else {
        CategoryColors[categoryExpense.category.id.toInt().mod(CategoryColors.size)]
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoryExpense.category.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (categoryExpense.percentage / 100).toFloat().coerceIn(0f, 1f))
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatIDR(categoryExpense.amount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${String.format("%.1f", categoryExpense.percentage)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
