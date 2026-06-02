# Expense Views and Statistics Category Pie Design

Date: 2026-05-31

## Scope

This change fixes category hierarchy creation and upgrades the Expense and Statistics screens:

- Adding a sub-category from a parent category must save as a child category, not as a top-level category.
- The Expense screen must expose five modes: Daily, Calendar, Monthly, Total, and Note.
- The Statistics screen must show category percentage pie charts for both income and expense.

The work stays inside the existing Kotlin, Jetpack Compose, MVVM, Room, and repository structure. No new chart dependency is required.

## Current Context

Categories already support hierarchy through `Category.parentId`, `CategoryDao.getSubCategories`, and `CategoryListViewModel` grouping. The add/edit category form also has parent state, but the sub-category flow needs to be made reliable from navigation through save.

Transactions currently load only the current month in `TransactionListViewModel`. The screen supports search and type chips, but it does not have date view modes or grouped daily/monthly/calendar summaries.

Statistics already computes expense totals by category and shows a stacked-bar style visual. It does not yet provide a real pie chart and does not compute income category percentages.

## Design

### Category Sub-Category Fix

The add sub-category path remains:

1. User opens the parent category menu.
2. User taps "Tambah Sub-Kategori".
3. App navigates to `categories/add?parentId=<parentId>`.
4. `AddEditCategoryScreen` loads the parent.
5. `AddEditCategoryViewModel.save()` inserts a `Category` with `parentId` set.

The form will preserve the loaded parent fields. For a new sub-category, `parentId`, transaction type, and color come from the parent. The screen title and helper text continue to show "Tambah Sub-Kategori" and "Sub-kategori dari: <parent>" so the user can verify the mode before saving.

If a parent cannot be loaded, the form should fail closed by showing an error event instead of silently saving as a top-level category.

### Expense Screen Modes

Add an `ExpenseViewMode` enum:

- `DAILY`
- `CALENDAR`
- `MONTHLY`
- `TOTAL`
- `NOTE`

The top area will include period navigation and a tab row with Daily, Calendar, Monthly, Total, and Note. The selected mode controls both the transaction date range and the presentation:

- Daily: Group transactions by date within the active month. Each group shows daily income, expense, net total, then transaction rows.
- Calendar: Render a month grid. Each day cell shows day number plus income, expense, and net total when present.
- Monthly: Show month-level rows for the selected year. The active month can expand into weekly rows like the reference image.
- Total: Show aggregate totals for the selected year/month context: income, expense, and balance, with simple rows suitable for quick scanning.
- Note: Show transactions with a non-blank note in the active month, grouped by date and still editable by tapping a row.

Search and type filters remain available where they make sense for list modes. Calendar and summary modes prioritize the date aggregation, with type totals shown in the cells/rows.

### Statistics Pie Charts

The Statistics screen will add a transaction type selector:

- Expense
- Income

The selected type determines:

- Pie chart segments.
- Percentage labels.
- Category detail list.
- Total amount caption.

The view model will compute two lists:

- `expenseCategoryBreakdown`
- `incomeCategoryBreakdown`

Each item contains category, amount, percentage, and color. Percentages are calculated against the selected type total for the active month. Categories with zero totals are omitted.

The pie chart will be a custom Compose `Canvas`, using `drawArc` for segments. The list below the chart remains the source of exact values, so labels can stay compact and readable on small screens.

Transactions without a matching category should appear under an "Uncategorized" fallback if they exist.

## Data Flow

Expense screen:

1. UI state stores selected mode and selected anchor date.
2. View model derives the active date range from mode and anchor date.
3. Repository streams transactions for the date range.
4. View model computes grouped summaries for the active mode.
5. Compose renders the selected mode without touching the database directly.

Statistics screen:

1. Repository streams current-month transactions and categories.
2. View model joins transactions to categories in memory.
3. View model emits separate income and expense breakdowns.
4. UI renders selector, pie chart, and category rows.

## Error Handling

- Empty transaction states should show concise empty messages per mode.
- Empty pie data should show an empty state instead of a blank chart.
- Invalid sub-category parent loads should emit a visible error and prevent accidental top-level save.
- Existing user data must not be migrated or rewritten.

## Testing and Verification

Primary verification:

- Build with `.\gradlew.bat assembleDebug`.
- Confirm sub-category save creates a row with non-null `parentId`.
- Confirm Expense tabs switch without crashing and reflect the active period.
- Confirm Statistics can switch between Expense and Income breakdowns.

Focused logic tests should be added if the aggregation code is extracted into pure helper functions. If the implementation remains tightly coupled to Compose view models and repositories, build verification plus manual logic review is acceptable for this scope.

## Non-Goals

- No account screen redesign.
- No database schema migration.
- No new analytics dependency.
- No export/reporting change.
- No full visual clone of the reference app; the behavior and hierarchy matter more than pixel-perfect copying.
