# CuangX Calm Ledger Redesign Design

## Summary

CuangX Finance will be redesigned as a Calm Ledger experience: a light-first, mature, finance-focused Android app that is easy to scan every day. The redesign covers the full UI surface while preserving existing features and behavior.

Approved direction:

- Visual style: Calm Ledger
- Density: Balanced daily app
- Palette: Navy + Green
- App structure: Dashboard Command Center
- Implementation approach: Design-system + main-flow pass
- Constraint: no functional or feature changes

## Goals

- Make CuangX feel like one cohesive product instead of a set of separate feature screens.
- Improve readability for money, dates, category names, portfolio values, and debt status.
- Keep daily transaction entry and review fast.
- Make dashboard feel like the command center for accounts, spending, portfolio, and dues.
- Create reusable Compose patterns for cards, lists, forms, empty states, loading states, and navigation polish.

## Non-Goals

- No Room schema changes.
- No repository contract changes.
- No ViewModel business logic rewrites unless required to connect existing UI data.
- No feature removal.
- No route restructuring except fixing UI navigation bugs or replacing placeholders with existing screens.
- No new backend, cloud sync, account system, or analytics stack.

## Visual Language

The app will use a calm financial palette:

- Background: soft slate off-white, close to `#F8FAFC`.
- Surface: white cards and sheets.
- Primary: trust navy, used for app identity, selected navigation, key headings, and primary actions.
- Success/profit: green, used for income, gains, healthy states, and positive deltas.
- Expense/loss/destructive: red, used only for expenses, losses, delete, and errors.
- Warning/due: amber, used for due dates, nearing limits, and attention states.
- Muted text: slate gray for metadata, helper text, and secondary labels.

The app should not become monochrome green. Green is semantic money/profit, while navy carries brand trust and structure.

Typography should emphasize money scanning:

- Large net worth and portfolio numbers use strong weight.
- Metadata stays compact but readable.
- Amounts align consistently on the right in ledger-style rows.
- Copy remains concise and Indonesian-first where the current UI already uses Indonesian.

## Component System

### App Shell

- Use a consistent screen background.
- Keep content clear of bottom navigation and gesture areas.
- Avoid nested card-in-card layouts.
- Prefer section groups with clear labels and controlled spacing.

### Cards

- Radius around 14-16dp.
- Soft border and minimal elevation.
- Cards represent meaningful data groups: net worth, cashflow, portfolio snapshot, due dates, budget status.
- Avoid using cards as decoration around every element.

### Lists

All financial list rows should follow a shared rhythm:

- Left: icon, category marker, or status indicator.
- Middle: primary label and metadata.
- Right: amount, status, or action summary.
- Rows must be tappable with visible press feedback.
- Amount colors must be semantic and supported by signs or labels, not color alone.

This applies to transactions, journal entries, holdings, accounts, budget rows, recurring rows, and debt rows.

### Forms

- Use visible labels, not placeholder-only inputs.
- Keep helper/error text near the field.
- Use numeric keyboards for amount, quantity, price, fee, rate, and days.
- Keep primary save action obvious and full-width where appropriate.
- Show loading state while saving.
- Do not add multi-step flows unless the current screen is already too dense to remain usable.

### States

- Empty states should tell the user what is missing and provide the existing primary action.
- Loading states should use shimmer or skeleton patterns where data takes longer than a moment.
- Error states should be local, specific, and recoverable.
- Destructive actions should use confirmation where data loss is possible.

## Screen Design

### Dashboard

Dashboard becomes the command center.

Core sections:

- Net worth hero card.
- Monthly cashflow snapshot.
- Portfolio snapshot.
- Upcoming dues from debt/receivable and recurring obligations.
- Recent transactions.

Dashboard should answer: "How am I doing financially right now?" without forcing the user into separate modules first.

### Transactions

Transactions become a modern ledger.

Design priorities:

- Date grouping or clear date metadata.
- Filter chips for type/account/category where current UI supports filtering.
- Amount aligned right.
- Income, expense, and transfer states visually distinct.
- Add transaction action remains fast and obvious.

### Journal and Portfolio

Journal remains the source of truth for investment actions. Portfolio becomes the summary and analysis surface.

Design priorities:

- Journal rows show action, asset, quantity, price, account, and total.
- Portfolio overview summarizes current position values and P&L.
- Holding detail should use the existing holding/journal data instead of placeholder UI where possible.
- Charts support understanding but do not dominate the screen.

### Accounts, Budget, Debt, Recurring

These modules should share the same card/list language.

- Accounts emphasize balance, type, and archive/credit card metadata.
- Budget emphasizes progress, remaining amount, and period.
- Debt emphasizes party, remaining amount, due date, and status.
- Recurring emphasizes next execution date, frequency, amount, and account/category.

### Settings

Settings should become a calm control panel.

Design priorities:

- Group related settings.
- Security settings should feel serious and clear.
- Backup/restore placeholders should either remain visibly disabled or be connected only if current functionality exists.
- Avoid implying data safety features that are not implemented.

## Navigation and Interaction

Bottom navigation remains at five items:

- Dashboard
- Expense
- Journal
- Portfolio
- More

The redesign polishes selected state, icon clarity, spacing, labels, and touch targets. More remains the entry point for secondary modules such as accounts, categories, budget, statistics, recurring, debt, and settings.

Interaction quality bar:

- Minimum 48dp touch targets.
- Clear pressed and selected states.
- One primary action per screen.
- Predictable back behavior.
- Loading feedback during async actions.
- Confirmation for destructive data loss.

## Accessibility

The redesign must preserve usability under real Android conditions:

- Contrast meets WCAG AA for text and meaningful UI.
- Text remains readable with system font scaling.
- No important financial meaning is communicated by color alone.
- Touch targets are large enough for mobile use.
- Screen content works at 360dp width without horizontal overflow.
- Buttons and icon-only controls have meaningful content descriptions.

## Implementation Boundaries

Allowed changes:

- `app/src/main/java/com/cuangx/finance/core/ui/theme/*`
- Shared UI components under `core/ui/components`
- Screen-level Compose layout and styling
- Navigation polish that keeps route intent intact
- UI copy improvements where they clarify existing behavior
- Replacing placeholder UI with existing implemented screens/data

Avoided changes:

- Database schema and migrations
- Repository interfaces and business contracts
- Data calculations
- Feature additions not required by existing behavior
- Large architecture refactors unrelated to UI quality

## Implementation Order

Recommended pass:

1. Design tokens: colors, typography, shapes, spacing helpers.
2. Shared components: cards, money text, list rows, section headers, empty/loading states.
3. App shell and bottom navigation.
4. Dashboard.
5. Transactions.
6. Journal and portfolio.
7. Accounts, budget, recurring, debt.
8. Forms and settings.
9. Visual QA and compile verification.

## Verification

Before considering the redesign complete:

- Build the Android app.
- Check major screens at narrow mobile width.
- Verify no feature path disappeared from navigation.
- Verify existing add/edit/save flows still call the same ViewModel actions.
- Scan for contrast and touch target regressions.
- Check empty/loading states for modules with no data.

