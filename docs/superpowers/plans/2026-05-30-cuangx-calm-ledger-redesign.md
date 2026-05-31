# CuangX Calm Ledger Redesign Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Redesign the full CuangX Finance Android UI into the approved Calm Ledger direction without changing existing features, data contracts, or business behavior.

**Architecture:** Keep the current single-module Android app, MVVM screens, Hilt ViewModels, repositories, Room schema, and navigation concepts. Add a small shared UI component layer under `core/ui/components`, refresh Material theme tokens, then migrate screen layouts to the shared Calm Ledger patterns.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, AndroidX Navigation Compose, Hilt, Room, DataStore, WorkManager.

---

## Execution Notes

- Do not implement directly on `master` unless the user explicitly approves that. Create an isolated worktree or branch before touching code.
- Preserve existing ViewModel calls and repository contracts.
- Do not change Room entities, DAOs, database version, or migrations.
- Do not remove existing routes or feature entry points.
- Prefer Compose-only UI changes.
- Verification command after UI tasks: `.\gradlew.bat assembleDebug`.
- If `assembleDebug` fails because Java is not available, record the exact `JAVA_HOME` or `java` error and continue only with static checks.

## File Map

### Theme and Tokens

- Modify: `app/src/main/java/com/cuangx/finance/core/ui/theme/Color.kt`
  - Calm Ledger palette and semantic colors.
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/theme/Theme.kt`
  - Light/dark Material color schemes and system bar behavior.
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/theme/Typography.kt`
  - More finance-friendly type scale using system font.
- Create: `app/src/main/java/com/cuangx/finance/core/ui/theme/Dimensions.kt`
  - Shared dp constants for radius, spacing, icon sizes, and row heights.

### Shared Components

- Create: `app/src/main/java/com/cuangx/finance/core/ui/components/CalmCard.kt`
  - Card variants for surface, hero, warning, and subtle sections.
- Create: `app/src/main/java/com/cuangx/finance/core/ui/components/FinanceText.kt`
  - `MoneyText`, `DeltaText`, and amount sign helpers.
- Create: `app/src/main/java/com/cuangx/finance/core/ui/components/FinanceListRow.kt`
  - Reusable row pattern for transactions, journal entries, accounts, debt, budgets, and holdings.
- Create: `app/src/main/java/com/cuangx/finance/core/ui/components/SectionHeader.kt`
  - Consistent section title + optional action.
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/components/EmptyState.kt`
  - Calm Ledger empty state styling.
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/components/ShimmerLoading.kt`
  - Match Calm Ledger surfaces if present.

### App Shell and Navigation

- Modify: `app/src/main/java/com/cuangx/finance/MainActivity.kt`
  - Ensure scaffold background uses theme background and bottom padding remains intact.
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/navigation/BottomNavBar.kt`
  - Calm Ledger selected state, nav bar elevation, labels, and colors.
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/navigation/AppNavHost.kt`
  - Replace holding detail placeholder route with real `HoldingDetailScreen` only if dependencies can be injected safely. Otherwise keep route behavior unchanged and style placeholder consistently.

### Primary Screens

- Modify: `app/src/main/java/com/cuangx/finance/feature/dashboard/DashboardScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/transaction/TransactionListScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/portfolio/journal/JournalListScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/portfolio/overview/PortfolioOverviewScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/portfolio/holding/HoldingDetailScreen.kt`

### Secondary Screens

- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/account/AccountListScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/category/CategoryListScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/budget/BudgetScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/recurring/RecurringScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/debt/DebtListScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/navigation/MoreScreen.kt`

### Forms and Settings

- Modify: add/edit screens under:
  - `app/src/main/java/com/cuangx/finance/feature/expense/account`
  - `app/src/main/java/com/cuangx/finance/feature/expense/category`
  - `app/src/main/java/com/cuangx/finance/feature/expense/transaction`
  - `app/src/main/java/com/cuangx/finance/feature/expense/budget`
  - `app/src/main/java/com/cuangx/finance/feature/expense/recurring`
  - `app/src/main/java/com/cuangx/finance/feature/portfolio/journal`
  - `app/src/main/java/com/cuangx/finance/feature/portfolio/holding`
  - `app/src/main/java/com/cuangx/finance/feature/debt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/settings/SettingsScreen.kt`

---

## Task 1: Prepare Isolated Work and Baseline

**Files:**
- Read: `docs/superpowers/specs/2026-05-30-cuangx-calm-ledger-redesign-design.md`
- Read: `git status --short`

- [ ] **Step 1: Confirm current branch and dirty state**

Run:

```powershell
git branch --show-current
git status --short
```

Expected:

- Current branch is visible.
- Existing untracked `.superpowers/` visual companion artifacts may exist.
- No unrelated tracked files are modified by this task.

- [ ] **Step 2: Create isolated worktree or branch**

If the user approves a branch in the current checkout, run:

```powershell
git switch -c redesign/calm-ledger
```

If the user wants a worktree, run:

```powershell
git worktree add ..\cuangx-finance-calm-ledger -b redesign/calm-ledger
```

Expected:

- Development happens outside `master`.

- [ ] **Step 3: Run baseline build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

Expected:

- If Java is configured, build result is recorded.
- If Java is missing, the expected known failure is:

```text
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
```

- [ ] **Step 4: Commit no changes**

No commit is created in this task.

---

## Task 2: Add Calm Ledger Design Tokens

**Files:**
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/theme/Color.kt`
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/theme/Theme.kt`
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/theme/Typography.kt`
- Create: `app/src/main/java/com/cuangx/finance/core/ui/theme/Dimensions.kt`

- [ ] **Step 1: Replace `Color.kt` palette**

Replace the semantic color section with these tokens while preserving the package/imports:

```kotlin
// Calm Ledger light theme
val LightPrimary = Color(0xFF0F172A)
val LightSecondary = Color(0xFF1E3A5F)
val LightCta = Color(0xFF166534)
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceMuted = Color(0xFFF1F5F9)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF0F172A)
val LightOnSurface = Color(0xFF0F172A)
val LightOnSurfaceMuted = Color(0xFF64748B)
val LightOutline = Color(0xFFE2E8F0)
val LightError = Color(0xFFDC2626)

// Calm Ledger dark theme
val DarkPrimary = Color(0xFFE2E8F0)
val DarkSecondary = Color(0xFF94A3B8)
val DarkCta = Color(0xFF22C55E)
val DarkBackground = Color(0xFF020617)
val DarkSurface = Color(0xFF0F172A)
val DarkSurfaceMuted = Color(0xFF111827)
val DarkOnPrimary = Color(0xFF0F172A)
val DarkOnBackground = Color(0xFFF8FAFC)
val DarkOnSurface = Color(0xFFF8FAFC)
val DarkOnSurfaceMuted = Color(0xFFCBD5E1)
val DarkOutline = Color(0xFF334155)
val DarkError = Color(0xFFEF4444)

// Semantic colors
val IncomeColor = Color(0xFF15803D)
val ExpenseColor = Color(0xFFDC2626)
val TransferColor = Color(0xFF2563EB)
val DebtColor = Color(0xFFD97706)
val ReceivableColor = Color(0xFF15803D)
val ProfitColor = Color(0xFF15803D)
val LossColor = Color(0xFFDC2626)
val WarningColor = Color(0xFFD97706)
val InfoColor = Color(0xFF2563EB)
val NeutralAmountColor = Color(0xFF334155)

// Category colors
val CategoryColors = listOf(
    Color(0xFF166534), Color(0xFF2563EB), Color(0xFFD97706),
    Color(0xFF7C3AED), Color(0xFFDB2777), Color(0xFF0F766E),
    Color(0xFFCA8A04), Color(0xFFDC2626), Color(0xFF4F46E5),
    Color(0xFF65A30D), Color(0xFF0891B2), Color(0xFFBE123C)
)
```

- [ ] **Step 2: Create `Dimensions.kt`**

Create:

```kotlin
package com.cuangx.finance.core.ui.theme

import androidx.compose.ui.unit.dp

object CuangXSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
}

object CuangXRadius {
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
}

object CuangXSize {
    val iconSm = 18.dp
    val iconMd = 22.dp
    val iconLg = 28.dp
    val touchTarget = 48.dp
    val listIcon = 40.dp
}
```

- [ ] **Step 3: Update `Theme.kt` color schemes**

Set light scheme values:

```kotlin
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = Color(0xFFE6EEF8),
    onPrimaryContainer = LightPrimary,
    secondary = LightSecondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8EEF6),
    onSecondaryContainer = LightSecondary,
    tertiary = LightCta,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDCFCE7),
    onTertiaryContainer = Color(0xFF14532D),
    error = LightError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceMuted,
    onSurfaceVariant = LightOnSurfaceMuted,
    outline = LightOutline,
    outlineVariant = Color(0xFFCBD5E1)
)
```

Set dark scheme values:

```kotlin
private val DarkColorScheme = darkColorScheme(
    primary = DarkCta,
    onPrimary = DarkOnPrimary,
    primaryContainer = Color(0xFF14532D),
    onPrimaryContainer = Color(0xFFDCFCE7),
    secondary = DarkSecondary,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF60A5FA),
    onTertiary = Color(0xFF0F172A),
    tertiaryContainer = Color(0xFF1E3A8A),
    onTertiaryContainer = Color(0xFFDBEAFE),
    error = DarkError,
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceMuted,
    onSurfaceVariant = DarkOnSurfaceMuted,
    outline = DarkOutline,
    outlineVariant = Color(0xFF475569)
)
```

- [ ] **Step 4: Update `Typography.kt` weights and line heights**

Keep `FontFamily.Default`, but ensure money-friendly hierarchy:

```kotlin
displayLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 34.sp,
    lineHeight = 40.sp
),
headlineLarge = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 26.sp,
    lineHeight = 32.sp
),
titleMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp
),
bodyMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp
),
labelSmall = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 11.sp,
    lineHeight = 14.sp
)
```

- [ ] **Step 5: Build or static compile check**

Run:

```powershell
.\gradlew.bat assembleDebug
```

Expected:

- PASS if Java is configured.
- Otherwise record the known Java environment failure.

- [ ] **Step 6: Commit**

Run:

```powershell
git add app/src/main/java/com/cuangx/finance/core/ui/theme
git commit -m "style: add calm ledger theme tokens"
```

---

## Task 3: Add Shared Calm Ledger Components

**Files:**
- Create: `app/src/main/java/com/cuangx/finance/core/ui/components/CalmCard.kt`
- Create: `app/src/main/java/com/cuangx/finance/core/ui/components/FinanceText.kt`
- Create: `app/src/main/java/com/cuangx/finance/core/ui/components/FinanceListRow.kt`
- Create: `app/src/main/java/com/cuangx/finance/core/ui/components/SectionHeader.kt`
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/components/EmptyState.kt`

- [ ] **Step 1: Create `CalmCard.kt`**

```kotlin
package com.cuangx.finance.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cuangx.finance.core.ui.theme.CuangXRadius
import com.cuangx.finance.core.ui.theme.CuangXSpacing

@Composable
fun CalmCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    contentPadding: PaddingValues = PaddingValues(CuangXSpacing.md),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = CardDefaults.shape.copy(all = androidx.compose.foundation.shape.CornerSize(CuangXRadius.lg)),
        colors = colors,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun HeroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    CalmCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(CuangXSpacing.xl),
        content = content
    )
}

@Composable
fun WarningCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    CalmCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF7ED),
            contentColor = Color(0xFF7C2D12)
        ),
        content = content
    )
}
```

- [ ] **Step 2: Create `FinanceText.kt`**

```kotlin
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
```

- [ ] **Step 3: Create `FinanceListRow.kt`**

```kotlin
package com.cuangx.finance.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.cuangx.finance.core.ui.theme.CuangXSize
import com.cuangx.finance.core.ui.theme.CuangXSpacing

@Composable
fun FinanceListRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null
) {
    val cardContent: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CuangXSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(CuangXSize.listIcon)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = iconTint,
                    modifier = Modifier.size(CuangXSize.iconSm)
                )
            }
            Spacer(modifier = Modifier.width(CuangXSpacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!supporting.isNullOrBlank()) {
                    Text(
                        text = supporting,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.width(CuangXSpacing.sm))
            trailing()
        }
    }

    if (onClick == null) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
            content = { cardContent() }
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            onClick = onClick,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
            content = { cardContent() }
        )
    }
}
```

- [ ] **Step 4: Create `SectionHeader.kt`**

```kotlin
package com.cuangx.finance.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        if (actionText != null && onAction != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}
```

- [ ] **Step 5: Update `EmptyState.kt`**

Change root padding to `CuangXSpacing.xxl`, icon size to `56.dp`, title style to `titleLarge`, and action button text to `labelLarge`. Import `CuangXSpacing`.

- [ ] **Step 6: Build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

Expected:

- PASS or known Java environment failure.

- [ ] **Step 7: Commit**

```powershell
git add app/src/main/java/com/cuangx/finance/core/ui/components
git commit -m "feat: add calm ledger shared components"
```

---

## Task 4: Polish App Shell and Bottom Navigation

**Files:**
- Modify: `app/src/main/java/com/cuangx/finance/MainActivity.kt`
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/navigation/BottomNavBar.kt`

- [ ] **Step 1: Set scaffold container background in `MainActivity.kt`**

Change the main `Scaffold` call to include:

```kotlin
containerColor = MaterialTheme.colorScheme.background
```

Add import:

```kotlin
import androidx.compose.material3.MaterialTheme
```

- [ ] **Step 2: Update `BottomNavBar.kt` NavigationBar colors**

Set:

```kotlin
NavigationBar(
    modifier = modifier,
    containerColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface,
    tonalElevation = 0.dp
)
```

Add import:

```kotlin
import androidx.compose.ui.unit.dp
```

- [ ] **Step 3: Update selected indicator colors**

Use:

```kotlin
colors = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    indicatorColor = MaterialTheme.colorScheme.primaryContainer
)
```

- [ ] **Step 4: Build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

- [ ] **Step 5: Commit**

```powershell
git add app/src/main/java/com/cuangx/finance/MainActivity.kt app/src/main/java/com/cuangx/finance/core/ui/navigation/BottomNavBar.kt
git commit -m "style: polish calm ledger app shell"
```

---

## Task 5: Redesign Dashboard Command Center

**Files:**
- Modify: `app/src/main/java/com/cuangx/finance/feature/dashboard/DashboardScreen.kt`

- [ ] **Step 1: Import shared components**

Add imports:

```kotlin
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.core.ui.components.DeltaText
import com.cuangx.finance.core.ui.components.FinanceListRow
import com.cuangx.finance.core.ui.components.HeroCard
import com.cuangx.finance.core.ui.components.MoneyText
import com.cuangx.finance.core.ui.components.SectionHeader
import com.cuangx.finance.core.ui.components.signedTransactionAmount
import com.cuangx.finance.core.ui.components.transactionAmountColor
import com.cuangx.finance.core.ui.theme.CuangXSpacing
```

- [ ] **Step 2: Update top app bar**

Use title:

```kotlin
title = {
    Column {
        Text("CuangX", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Financial command center", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
```

- [ ] **Step 3: Replace `NetWorthCard` with hero style**

Update `NetWorthCard` implementation:

```kotlin
@Composable
private fun NetWorthCard(netWorth: Double) {
    HeroCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Total Net Worth",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f)
        )
        Spacer(modifier = Modifier.height(CuangXSpacing.xs))
        MoneyText(
            amount = netWorth,
            emphasized = true,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(CuangXSpacing.xs))
        Text(
            text = if (netWorth >= 0) "Accounts + portfolio + receivables - debt" else "Debt currently exceeds tracked assets",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f)
        )
    }
}
```

- [ ] **Step 4: Replace section header row**

Use:

```kotlin
SectionHeader(
    title = "Recent Transactions",
    actionText = "See All",
    onAction = onNavigateToTransactions
)
```

- [ ] **Step 5: Replace `RecentTransactionItem` body with `FinanceListRow`**

Use:

```kotlin
FinanceListRow(
    icon = typeIcon,
    iconTint = typeColor,
    title = transaction.note.ifEmpty { transaction.type.displayName },
    subtitle = DateUtils.getRelativeDateLabel(transaction.date),
    trailing = {
        Text(
            text = signedTransactionAmount(transaction.type, transaction.amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = transactionAmountColor(transaction.type)
        )
    }
)
```

- [ ] **Step 6: Keep existing dashboard data flow**

Do not modify:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

Do not rename `DashboardScreen` parameters.

- [ ] **Step 7: Build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

- [ ] **Step 8: Commit**

```powershell
git add app/src/main/java/com/cuangx/finance/feature/dashboard/DashboardScreen.kt
git commit -m "style: redesign dashboard command center"
```

---

## Task 6: Redesign Transactions Ledger

**Files:**
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/transaction/TransactionListScreen.kt`

- [ ] **Step 1: Add shared component imports**

```kotlin
import com.cuangx.finance.core.ui.components.CalmCard
import com.cuangx.finance.core.ui.components.FinanceListRow
import com.cuangx.finance.core.ui.components.SectionHeader
import com.cuangx.finance.core.ui.components.signedTransactionAmount
import com.cuangx.finance.core.ui.components.transactionAmountColor
import com.cuangx.finance.core.ui.theme.CuangXSpacing
```

- [ ] **Step 2: Keep search and filter behavior**

Preserve these calls exactly:

```kotlin
onValueChange = viewModel::updateSearchQuery
onClick = { viewModel.updateSelectedType(TransactionType.INCOME) }
onClick = { viewModel.updateSelectedType(TransactionType.EXPENSE) }
onClick = { viewModel.updateSelectedType(TransactionType.TRANSFER) }
onClick = { viewModel.clearFilters() }
```

- [ ] **Step 3: Update `MonthlySummaryCard` to `CalmCard`**

Use `CalmCard` with two equal columns, labels `Income` and `Expense`, and existing `CurrencyFormatter.formatIDR` values. Keep `totalIncome` and `totalExpense` parameters unchanged.

- [ ] **Step 4: Update `TransactionItem` to `FinanceListRow`**

Replace `Card` content with:

```kotlin
FinanceListRow(
    icon = typeIcon,
    iconTint = typeColor,
    title = transaction.note.ifEmpty { transaction.type.displayName },
    subtitle = DateUtils.getRelativeDateLabel(transaction.date),
    trailing = {
        Text(
            text = signedTransactionAmount(transaction.type, transaction.amount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = transactionAmountColor(transaction.type)
        )
    },
    onClick = onClick,
    contentDescription = transaction.type.displayName
)
```

- [ ] **Step 5: Replace empty text with `EmptyTransactions` when no filter is active**

When transactions are empty and no filter/search is active, call:

```kotlin
EmptyTransactions(onAddClick = onNavigateToAddTransaction)
```

When filters/search are active, keep a filter-specific text state:

```kotlin
Text(
    text = "Tidak ada transaksi yang sesuai filter",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(16.dp)
)
```

- [ ] **Step 6: Build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

- [ ] **Step 7: Commit**

```powershell
git add app/src/main/java/com/cuangx/finance/feature/expense/transaction/TransactionListScreen.kt
git commit -m "style: redesign transaction ledger"
```

---

## Task 7: Redesign Journal and Portfolio Screens

**Files:**
- Modify: `app/src/main/java/com/cuangx/finance/feature/portfolio/journal/JournalListScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/portfolio/overview/PortfolioOverviewScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/portfolio/holding/HoldingDetailScreen.kt`

- [ ] **Step 1: Update `JournalListScreen` empty state**

Replace custom empty `Box` with:

```kotlin
EmptyJournal(onAddClick = onNavigateToAddJournal)
```

Add import:

```kotlin
import com.cuangx.finance.core.ui.components.EmptyJournal
```

- [ ] **Step 2: Update `JournalEntryItem` to shared row**

Use `FinanceListRow` with:

```kotlin
title = "${entry.action.displayName} ${entry.name}"
subtitle = "${entry.quantity} x ${CurrencyFormatter.formatIDR(entry.price)}"
supporting = DateUtils.formatDate(entry.date)
```

Trailing:

```kotlin
Column(horizontalAlignment = Alignment.End) {
    Text(
        text = CurrencyFormatter.formatIDR(entry.quantity * entry.price),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold
    )
    if (entry.fee > 0) {
        Text(
            text = "Fee ${CurrencyFormatter.formatIDR(entry.fee)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

- [ ] **Step 3: Update portfolio summary hero**

In `PortfolioOverviewScreen.kt`, change `PortfolioSummaryCard` to use `HeroCard`, `MoneyText`, and `DeltaText` while preserving all parameters:

```kotlin
HeroCard(modifier = Modifier.fillMaxWidth()) {
    Text("Total Portfolio Value", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.78f))
    Spacer(modifier = Modifier.height(8.dp))
    MoneyText(amount = totalValue, emphasized = true, color = MaterialTheme.colorScheme.onPrimary)
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text("Invested", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
            Text(CurrencyFormatter.formatIDR(totalCost), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
        }
        DeltaText(amount = totalPnl, percent = totalPnlPercent)
    }
}
```

- [ ] **Step 4: Replace holdings empty text**

Use:

```kotlin
EmptyPortfolio(onAddClick = onNavigateToAddJournal)
```

- [ ] **Step 5: Update `HoldingItem` to shared row**

Use `FinanceListRow` with an asset icon already available or keep no new icon dependency by using `Icons.Default.ShowChart`. The trailing column must still show current value, P&L, and percent.

- [ ] **Step 6: Style `HoldingDetailScreen` cards**

Replace primary summary `Card` with `HeroCard`. Replace transaction history item cards with `FinanceListRow`. Do not change `LaunchedEffect(ticker)` or repository calls.

- [ ] **Step 7: Build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

- [ ] **Step 8: Commit**

```powershell
git add app/src/main/java/com/cuangx/finance/feature/portfolio
git commit -m "style: redesign journal and portfolio surfaces"
```

---

## Task 8: Redesign Secondary Module Lists

**Files:**
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/account/AccountListScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/category/CategoryListScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/budget/BudgetScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/recurring/RecurringScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/debt/DebtListScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/navigation/MoreScreen.kt`

- [ ] **Step 1: Apply `FinanceListRow` pattern to account rows**

For account rows, trailing content must show formatted balance. Subtitle must show account type and currency.

- [ ] **Step 2: Apply `FinanceListRow` pattern to category rows**

For category rows, trailing content can be empty or show parent/type metadata already present in the current screen.

- [ ] **Step 3: Apply `CalmCard` to budget progress cards**

Preserve existing progress calculation and use semantic colors for healthy/warning/exceeded states.

- [ ] **Step 4: Apply `FinanceListRow` to recurring rows**

Subtitle must include frequency and next date. Trailing must include amount.

- [ ] **Step 5: Apply `FinanceListRow` to debt rows**

Subtitle must include party and due date. Trailing must include remaining amount and status.

- [ ] **Step 6: Redesign `MoreScreen` as grouped menu**

Use `CalmCard` groups:

- Money Setup: Accounts, Categories
- Planning: Budget, Statistics, Recurring
- Obligations: Utang & Piutang
- App: Settings

Preserve all existing navigation callbacks.

- [ ] **Step 7: Build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

- [ ] **Step 8: Commit**

```powershell
git add app/src/main/java/com/cuangx/finance/feature/expense/account app/src/main/java/com/cuangx/finance/feature/expense/category app/src/main/java/com/cuangx/finance/feature/expense/budget app/src/main/java/com/cuangx/finance/feature/expense/recurring app/src/main/java/com/cuangx/finance/feature/debt app/src/main/java/com/cuangx/finance/core/ui/navigation/MoreScreen.kt
git commit -m "style: redesign secondary module lists"
```

---

## Task 9: Redesign Add/Edit Forms Without Behavior Changes

**Files:**
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/account/AddEditAccountScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/category/AddEditCategoryScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/transaction/AddEditTransactionScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/budget/AddEditBudgetScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/expense/recurring/AddEditRecurringScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/portfolio/journal/AddEditJournalScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/portfolio/holding/AddEditHoldingScreen.kt`
- Modify: `app/src/main/java/com/cuangx/finance/feature/debt/AddEditDebtScreen.kt`

- [ ] **Step 1: Standardize form screen content padding**

Use:

```kotlin
.padding(padding)
.padding(horizontal = 16.dp, vertical = 12.dp)
```

for scrollable form containers.

- [ ] **Step 2: Wrap related form sections in `CalmCard`**

Use cards for logical groups only:

- Basic information
- Amount and account
- Date and recurrence
- Notes and metadata

- [ ] **Step 3: Preserve every ViewModel update callback**

Do not rename or remove calls such as:

```kotlin
viewModel::updateName
viewModel::updateAmount
viewModel::updateNote
viewModel::save
```

Use the exact current callback names in each form.

- [ ] **Step 4: Keep primary save button behavior**

Each form must still call:

```kotlin
onClick = viewModel::save
```

or the existing save method for that ViewModel.

- [ ] **Step 5: Ensure numeric fields keep numeric keyboard**

For amount, price, quantity, rate, and days fields, preserve:

```kotlin
keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
```

or existing decimal numeric keyboard settings.

- [ ] **Step 6: Build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

- [ ] **Step 7: Commit**

```powershell
git add app/src/main/java/com/cuangx/finance/feature/expense app/src/main/java/com/cuangx/finance/feature/portfolio app/src/main/java/com/cuangx/finance/feature/debt
git commit -m "style: redesign add edit forms"
```

---

## Task 10: Redesign Settings and Data Safety Copy

**Files:**
- Modify: `app/src/main/java/com/cuangx/finance/feature/settings/SettingsScreen.kt`

- [ ] **Step 1: Add grouped setting cards**

Wrap each existing section in `CalmCard`:

- Security
- Appearance
- Finance
- Data
- About

- [ ] **Step 2: Preserve setting mutations**

Keep:

```kotlin
onCheckedChange = viewModel::setPasscodeEnabled
onCheckedChange = viewModel::setBiometricEnabled
viewModel.setDarkMode(value)
viewModel.setStartDay(selectedDay.toInt())
viewModel.setDefaultCurrency(currency)
```

- [ ] **Step 3: Make backup/restore disabled-looking if not implemented**

Keep callbacks as no-op, but change subtitles to make status truthful:

```kotlin
subtitle = "Export data to .xlsx file (not available yet)"
subtitle = "Import data from backup file (not available yet)"
```

The `onClick` values remain:

```kotlin
onClick = {}
```

- [ ] **Step 4: Build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

- [ ] **Step 5: Commit**

```powershell
git add app/src/main/java/com/cuangx/finance/feature/settings/SettingsScreen.kt
git commit -m "style: redesign settings surface"
```

---

## Task 11: Navigation Placeholder Pass

**Files:**
- Modify: `app/src/main/java/com/cuangx/finance/core/ui/navigation/AppNavHost.kt`
- Read: `app/src/main/java/com/cuangx/finance/feature/portfolio/holding/HoldingDetailScreen.kt`

- [ ] **Step 1: Check whether `HoldingDetailScreen` can be injected cleanly**

Current signature:

```kotlin
fun HoldingDetailScreen(
    ticker: String,
    onNavigateBack: () -> Unit,
    journalRepository: JournalRepository,
    priceRepository: PriceRepository
)
```

Because repositories are not defaulted with `hiltViewModel`, either:

- create a small `HoldingDetailViewModel`, or
- keep placeholder styled and defer logic connection.

For this no-feature-change redesign, prefer keeping route behavior stable unless the code already has a working ViewModel.

- [ ] **Step 2: Style placeholder if retained**

If placeholder remains, update its text to:

```kotlin
Text(
    text = "Holding detail for $ticker",
    style = MaterialTheme.typography.titleLarge,
    color = MaterialTheme.colorScheme.onBackground
)
```

Wrap it in `CalmCard` centered in a background-colored Box.

- [ ] **Step 3: Preserve account detail placeholder behavior**

Do not create account detail functionality in this redesign task. Style `PlaceholderScreen("Account Detail")` consistently if it remains.

- [ ] **Step 4: Build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

- [ ] **Step 5: Commit**

```powershell
git add app/src/main/java/com/cuangx/finance/core/ui/navigation/AppNavHost.kt
git commit -m "style: align placeholder screens with redesign"
```

---

## Task 12: Final Verification and Cleanup

**Files:**
- Read: all modified files from this plan.
- Modify only if verification finds UI regressions or compile errors.

- [ ] **Step 1: Run full build**

Run:

```powershell
.\gradlew.bat assembleDebug
```

Expected:

- PASS if Java is configured.
- If Java is unavailable, final report must say build could not be verified because Java is missing.

- [ ] **Step 2: Search for broken copy and unfinished implementation markers**

Run:

```powershell
rg "NotImplemented|throw NotImplemented|Ã|â" app/src/main/java
```

Expected:

- No new unresolved placeholders introduced by redesign.
- Existing intentional backup/restore no-op copy is truthful.
- Mojibake such as `Ã` or `â` should be fixed where touched.

- [ ] **Step 3: Check route coverage**

Run:

```powershell
rg "composable\\(" app/src/main/java/com/cuangx/finance/core/ui/navigation/AppNavHost.kt
```

Expected:

- Dashboard, transactions, journal, portfolio, more, settings, debts, accounts, categories, budget, recurring routes still exist.

- [ ] **Step 4: Check git status**

Run:

```powershell
git status --short
```

Expected:

- Only intended redesign files are modified.
- `.superpowers/` visual companion artifacts can remain untracked unless the user wants them removed or ignored.

- [ ] **Step 5: Commit final fixes if needed**

If Step 1-4 required small fixes, commit:

```powershell
git add app/src/main/java
git commit -m "chore: finalize calm ledger redesign"
```

If no fixes are needed, skip commit.

---

## Self-Review Checklist

- Spec coverage:
  - Theme tokens covered by Task 2.
  - Shared components covered by Task 3.
  - App shell/navigation covered by Task 4 and Task 11.
  - Dashboard command center covered by Task 5.
  - Transactions ledger covered by Task 6.
  - Journal/portfolio covered by Task 7.
  - Secondary modules covered by Task 8.
  - Forms/settings covered by Task 9 and Task 10.
  - Verification covered by Task 12.
- Placeholder scan:
  - This document has no unfinished marker text.
  - Each implementation step names a concrete target.
  - Backup/restore no-op behavior is explicitly preserved and made truthful.
- Type consistency:
  - Shared functions referenced later are defined in Task 3.
  - Existing ViewModel callbacks are preserved by instruction instead of renamed.
  - Route behavior is preserved unless an existing implementation can be connected safely.
