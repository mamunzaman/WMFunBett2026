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
- [x] Jackpot Chain V1: carry model, calculator, Add Entry payment breakdown
- [x] Add Entry amount enforcement: locked round stake, carry-in total, `currentRoundAmount` on Entry
- [x] Modal bottom sheets for create/edit actions (FormBottomSheet shell)
- [x] Light Material 3 form sheets + EN/DE string resources
- [x] Match-center UI redesign: blue header, match cards, 5-tab nav, dashboard
- [x] Game/Tipp detail match-center cards: expandable game overview, rich tipp/entry cards, person-in-game sheet
- [x] Match card polish: shared card with flags, status pills, glass bottom nav, Add Game form sections
- [x] Bottom sheet scrim polish, tipp overview mini cards, tipp detail stat chips
- [x] Tipp detail glass UI, dark premium sheets, equal sample entry amounts
- [x] Modal bottom sheet blurred glass backdrop (app shell + FormBottomSheet)
- [x] Premium match-center color system: palette tokens, gradients, reusable card components
- [x] Tipps dashboard polish: JackpotSummaryCard, MatchCard, TournamentCard, glass nav
- [x] Sheet blur polish, edge-to-edge header, nav glass readability layer
- [x] Clipped blur glass on bottom nav pill only (no dim overlay block)
- [x] Web match-center preview (`web/preview/`) with GSAP intro timeline + ScrollTrigger batch reveal
- [x] Main nav restructure: Matches, Leagues, + Tipps, Friends, Settings
- [x] Matchday header filter (not a nav step), grouped match lists
- [x] League list + league matches, Friends from entries, Tipps center sheet
- [x] Matches header LIVE pill, calendar select sheet, search actions
- [x] Shared screen content spacing (`screenContentPadding`, 16dp top)
- [x] Leagues screen staggered 2-column premium grid
- [x] League grid card premium M3 press interaction (scale, elevation, glow, ripple)
- [x] Custom League Create Round dialog (in-memory rounds in grid)
- [x] Matches screen staggered card entrance animation (fade + slide up)
- [x] Premium Create menu bottom sheet (+ action flow)
- [x] Create Round modal bottom sheet (replaces dialog)
- [x] Context-aware + menu (Matches/Leagues/League detail/Game detail)
- [x] Add Match bottom sheet with round selector
- [x] Add Match sheet: date/time pickers, tipp type, all leagues in selector
- [x] Add Match inside league: locked league info row, no dropdown
- [x] Add Tipp Group bottom sheet: tipp type, entry amount, note (in-memory)
- [x] Hierarchy cleanup: tipp type on Tipp Group only, not on Match
- [x] Add Match Day/Time picker fields open Material pickers on tap
- [x] Add Match Team A/B: flag badge, country autocomplete, name normalization
- [x] Add Match past-date blocked (picker + validation)
- [x] + menu Tipp Group targets current game via nav arguments
- [x] Unified addTippGroup path; Second Half allowed pre-match via menu rules
- [x] Real Add Entry sheet on Tipp Group detail only (in-memory)
- [x] Entry amount locked to Tipp Group entryAmount (no per-entry override)
- [x] Room database foundation: entities, DAOs, mappers, FunBettDatabase v1
- [x] Persist leagues, matches, tipp groups, entries across app restart
- [x] Sample data seeds only when database is empty
- [x] Friends foundation: FriendEntity, FriendDao, local participants only
- [x] Friends screen: count, list, empty state, Add Friend sheet
- [x] Entry stores friendId + friendName; Add Entry uses friend selector
- [x] Friend financial tracking: summary + entry history detail sheet
- [x] EntryCard reusable component; Tipp Group entry table (Pick/Predict/Current/Stake)
- [x] All-friends-joined gating: info card on Tipp Group detail, center + menu info
- [x] Friends sort chip: compact A–Z control aligned with grid/list toggle

## In Progress
- None

## Pending Tasks
- [ ] ViewModels and repositories
- [ ] Edit actions (friends, entries)
- [ ] Jackpot management business logic
- [ ] Winner and scoring logic (jackpot carry-forward, paid status)
- [ ] Room migrations (when schema changes)

- [x] Tipp Group entry table winner highlighting (exact score match, multi-winner)

- [x] Jackpot carry-over foundation (display-only, per tipp type)
- [x] Add Tipp Group menu rules: NOT_STARTED unused types; LIVE/FINISHED blocked
- [x] Entry edit/delete: row menu, EditEntrySheet, repository updateEntry/deleteEntry
- [x] Friends screen compact FIFA style; friend detail edit/delete menu
- [x] Friend/person first + last name (Vorname/Nachname): forms, display rules, backward compat
- [x] Add/Edit Entry score prediction UI (Design B): match preview + dual score inputs
- [x] Entry participation foundation (Phase 1): `EntryParticipation`, Entry fields, split settlement read models
- [x] Jackpot calculator architecture (Phase 2): participation helpers, catch-up models, split summary docs (not wired)
- [x] Split winner engine architecture (Phase 3): split outcome models, payout pools, chain-break signal (not wired)
- [x] Entry participation persistence (Phase 4A): Room v3 migration, entity + mapper fields

## Last Update
2026-06-21 — Phase 4A: Entry participation + catch-up persisted in Room (v2→v3 migration).
