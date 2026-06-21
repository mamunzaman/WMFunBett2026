# WM Fun Bett 2026 — Project Status

## Completed Features
- [x] FIFA World Cup 2026 inspired design system (Color.kt, Theme.kt)
- [x] Dark theme as default application theme
- [x] Material 3 color scheme with custom palette
- [x] Dashboard placeholder screen (jackpot card + stats)
- [x] Bottom navigation shell (Home, Tipps, Kasse — 3 tabs)
- [x] Home overview and Kasse jackpot placeholder
- [x] Shared card components (JackpotCard, StatCard, PlaceholderCard)
- [x] Round → Day → Game → Tipp Group → Entry navigation flow
- [x] In-memory data models and FunBettRepository (offline hierarchy)
- [x] Game screen: teams, result display, match status, game kasse, tipp groups
- [x] Tipp Group screen: entries with name, prediction, amount, note, group total
- [x] Add Entry dialog with score UI and in-memory save
- [x] Hierarchy screens with top app bar, breadcrumbs, FAB add, 3-dot delete
- [x] Nested NavHost for WM 2026 tab (bottom bar always visible)
- [x] WM 2026 tab: add tournament, game, tipp group, entry via FAB
- [x] In-memory delete for tournament, game, tipp group, entry with confirm dialog
- [x] Add Game dialog: chip-style date/time pickers with custom fallback
- [x] Add Tipp Group dialog: auto title from time scope
- [x] Tipp scope availability: duplicate + time cutoff rules on add
- [x] Add Tipp Group crash fix: safe empty-scope handling
- [x] Add Game crash fix: Column/Row chip layout (no FlowRow)
- [x] Stability audit: immutable repo reads, scoped lookups, dialog reset on nav, not-found screens
- [x] Game result management: scores, MatchStatus, Set Result dialog (no winner calc yet)
- [x] Winner Engine V1: per tipp group, equal split, UI summary + entry highlight
- [x] Tipp group menus: card delete guard, Winner Share Settings placeholder

## In Progress
- None

## Pending Tasks
- [ ] Room database layer (replace FunBettRepository in-memory store)
- [ ] ViewModels and repositories
- [ ] Edit actions
- [ ] Jackpot management business logic
- [ ] Winner and scoring logic (jackpot carry-forward, paid status)

## Last Update
2026-06-21 — Winner Engine V1: per-group split, summary cards, entry highlight.
