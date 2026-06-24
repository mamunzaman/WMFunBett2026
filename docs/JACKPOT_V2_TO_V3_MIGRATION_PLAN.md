# WM Fun Bett 2026
# Jackpot V2 to V3 Migration Plan

**Status:** Planning only — no implementation in this document.  
**References:** [Jackpot Rules V2](JACKPOT_RULES_V2.md) · [Jackpot Rules V3](JACKPOT_RULES_V3.md)

---

## 1. Current State

| Branch / artifact | Contents |
|---|---|
| `main` | V2 foundation: dual-pool calculator, chain models, repository hooks, basic UI |
| `feature/main-calculation-logic-v1` | Extended V2: settlement builder, person-specific catch-up, Result Calculation UI |
| `feature/jackpot-rules-v3-main-pot` | V3 design document (`JACKPOT_RULES_V3.md`) |
| **Direction** | **V3 selected** as target model |

V2 remains in the codebase and docs until V3 is validated phase by phase. V2 files are **not deleted** during planning.

---

## 2. Why V2 Is Being Replaced

| Problem (V2) | Impact |
|---|---|
| Per–Tipp-Group jackpot chains (per time scope) | Hard to explain which pot applies to which round |
| Multiple active chains at once | Confusing balances and catch-up across Half Time / Full Time / Penalty |
| Automatic jackpot on any group with jackpot entries | Friends unclear when they are “in the jackpot” |
| Catch-up tied to scope-specific chains | Person-specific catch-up helps but chains remain complex |
| Future refund / delete / participation edge cases | Higher risk of wrong chain state |

**V3 is simpler:** one **Main Jackpot Pot**, organizer-controlled **Jackpot Rounds**, catch-up from **missed source Tipp Groups** (entry amounts, not pot balance).

---

## 3. V3 Target Concept

Summary aligned with [JACKPOT_RULES_V3.md](JACKPOT_RULES_V3.md).

| Concept | Rule |
|---|---|
| **Main Jackpot Pot** | Single round-level pot; all jackpot money lives here |
| **Normal Tipp Groups** | Default — local play, no jackpot switch on ordinary rounds |
| **No winner (normal)** | Full eligible Tipp Group pot → Main Jackpot Pot; source recorded |
| **Source Tipp Round tracking** | Game + Tipp Group + entry amount + contributed amount |
| **Catch-up history** | List of source rounds; catch-up = sum of **missed source entry amounts** (person-specific) |
| **Catch-up payment** | Paid on Jackpot Round join → **Main Jackpot Pot immediately** (not current round pot) |
| **Jackpot Round** | Organizer manually marks one Tipp Group “Use Main Jackpot Pot” |
| **One active Jackpot Round** | No second Jackpot Round until current one is settled |
| **Jackpot winner** | Pay current round pot + Main Jackpot Pot; reset pot and histories to €0 |
| **No jackpot winner** | Add current round pot to Main Jackpot Pot; append source to catch-up history |

**Hierarchy unchanged:** Round → Day → Game → Tipp Group → Entry.

---

## 4. V2 Code Areas to Inspect Later

Likely touch points when implementing V3. Inspect on `main` and `feature/main-calculation-logic-v1` before editing.

### Data / logic

| File | V2 role |
|---|---|
| `JackpotModels.kt` | V2 entry input, result, catch-up, settlement types |
| `JackpotV2Calculator.kt` | Dual-pool payout (current + incoming jackpot) |
| `JackpotV2SettlementBuilder.kt` | Incoming chain, catch-up, settle (extended branch) |
| `JackpotChainCalculator.kt` | Chain walk, participation breakdown |
| `TippGroupWinnerEngine.kt` | Winner detection; may delegate to V2 calculator |
| `FunBettRepository.kt` | `getTippGroupV2Settlement`, `getEntryJoinBreakdown`, payment snapshot |

### UI

| File | V2 role |
|---|---|
| `AddEntrySheet.kt` | Local / Join jackpot, catch-up preview |
| `EntryCard.kt` | Per-entry V2 payout labels |
| `GameDetailScreen.kt` | Compact V2 summaries on tipp cards |
| `TippGroupDetailScreen.kt` | Result Calculation section |
| `TippGroupV2UiHelpers.kt` | Compact summary formatting *(if present on branch)* |

### Tests

| File | V2 role |
|---|---|
| `JackpotV2CalculatorTest.kt` | Scenarios 1–6 dual-pool |
| `JackpotCatchUpTest.kt` | Global / chain catch-up *(extended branch)* |
| `JackpotCatchUpPersonTest.kt` | Person-specific catch-up *(extended branch)* |

**Strategy:** Add parallel V3 types/calculator/tests; do not rewrite V2 in place until Phase 5.

---

## 5. V3 Model Needs (Draft)

New concepts — names tentative, for Kotlin design later.

### `MainJackpotPot`

| Field | Purpose |
|---|---|
| `roundId` | Owning round |
| `currentAmount` | Live pot balance |
| `contributions` | Ledger of money in (source rounds, catch-up payments) |

### `JackpotSourceRound`

| Field | Purpose |
|---|---|
| `gameId` | Source game |
| `tippGroupId` | Source Tipp Group |
| `entryAmount` | Catch-up obligation amount (e.g. €10) |
| `contributedAmount` | Actual money added to Main Jackpot Pot |
| `reason` | `NO_WINNER_NORMAL` / `NO_WINNER_JACKPOT_ROUND` |

### `JackpotRoundStatus`

| Value | Meaning |
|---|---|
| `NONE` | No active Jackpot Round |
| `ACTIVE` | Jackpot Round open; entries / result pending |
| `SETTLED` | Result processed; lock released |

### `JackpotJoinBreakdown`

| Field | Purpose |
|---|---|
| `currentAmount` | Current Tipp Group entry |
| `catchUpAmount` | Missed source entry amounts for this person |
| `totalDue` | current + catch-up |
| `catchUpDestination` | Always Main Jackpot Pot (V3 rule) |

### `JackpotSettlementResult`

| Field | Purpose |
|---|---|
| `currentPotTotal` | Current Jackpot Round pot |
| `mainJackpotTotal` | Main Jackpot Pot before payout |
| `currentWinners` | Correct winners (all entries) |
| `jackpotWinners` | Jackpot-qualified correct winners |
| `currentSharePerWinner` | Current pot split |
| `jackpotSharePerWinner` | Main Jackpot split |
| `mainJackpotAfter` | €0 on winner; increased on no winner |
| `catchUpHistoryAfter` | Cleared on winner; +1 source on no winner |

### `activeJackpotTippGroupId`

| Field | Purpose |
|---|---|
| `roundId` | Round scope |
| `tippGroupId` | The one Tipp Group marked as Jackpot Round while `ACTIVE` |
| `gameId` | Parent game (for UI / lock checks) |

**Note:** Persistence (Room) is out of scope until a dedicated schema phase — start in-memory / sample data.

---

## 6. UI Migration Plan

Future UI only — no changes in this planning step.

### Main Jackpot visibility

| Location | Future content |
|---|---|
| Game detail | Main Jackpot amount + “active Jackpot Round?” indicator |
| League / Matches | Optional summary card *(later)* |
| Kasse tab | Optional jackpot balance *(later)* |

### Tipp Group result section

| Outcome | Display / action |
|---|---|
| Normal round + winner(s) | Winner payout (current pot only) |
| Normal round + no winner | “Added €X to Main Jackpot” + source recorded |
| Jackpot Round + winner(s) | Current pot split + Main Jackpot split |
| Jackpot Round + no winner | Current pot added to Main Jackpot; catch-up history updated |

### Add Entry — normal mode

| Behavior |
|---|
| No Local / Join jackpot switch |
| Single entry amount = Tipp Group `entryAmount` |
| No catch-up line |

### Add Entry — Jackpot Round mode

| Line | Source |
|---|---|
| Current | Tipp Group entry amount |
| Catch-up | Person-specific missed source entry amounts |
| Total | Current + catch-up |
| Note | Catch-up goes to Main Jackpot Pot on save |

### Organizer actions

| Action | Preconditions |
|---|---|
| **Add to Main Jackpot** | Automatic on normal no-winner settlement *(or confirm step)* |
| **Start Jackpot Round** | Main Jackpot Pot **> 0**; **no** active Jackpot Round; organizer picks **one** Tipp Group |
| **Settle Jackpot Round** | Result entered; drives winner payout or pot growth |

### V2 UI to retire (Phase 5)

- Per-scope incoming jackpot on tipp cards
- Local / Join jackpot toggle on non–Jackpot Rounds
- V2 “Result Calculation” dual-pool labels where replaced by V3 settlement

---

## 7. Test Migration Plan

V3 tests should mirror [V3 Test Scenarios](JACKPOT_RULES_V3.md#v3-test-scenarios) in `JackpotV3CalculatorTest.kt` (name TBD).

| # | Test case | Expected |
|---|---|---|
| 1 | No winner adds full pot to Main Jackpot | Normal round €30 pot → Main Jackpot €30; catch-up history €10 |
| 2 | Winner pays normal pot only | Main Jackpot unchanged; winners split current pot |
| 3 | Multiple Tipp Rounds across games grow Main Jackpot | €30 + €20 + €40 = €90; catch-up €30 |
| 4 | Catch-up uses missed source entry amounts | Person missed 3×€10 → catch-up €30 (not pot €90) |
| 5 | Catch-up goes directly to Main Jackpot | Join €40 → €30 Main Jackpot, €10 current round pot |
| 6 | Jackpot winner splits current pot and Main Jackpot | €30 / 2 = €15; €130 / 2 = €65; pot reset €0 |
| 7 | Jackpot no winner adds current pot to Main Jackpot | €90 + €30 catch-up + €30 round → €150; +€10 history |
| 8 | One active Jackpot Round lock | Cannot start second round until first settled |

Keep all V2 tests green until Phase 5; add V3 tests alongside in Phase 1.

---

## 8. Safe Implementation Phases

| Phase | Scope | Risk |
|---|---|---|
| **1 — Pure logic** | `JackpotV3*` models + calculator + unit tests only | Low — no UI/repo |
| **2 — Repository** | In-memory / sample-data wiring; read APIs for pot, catch-up, settlement | Low — no Room |
| **3 — Display** | Show Main Jackpot + V3 settlement on Game / Tipp Group screens | Medium — read-only |
| **4 — Organizer actions** | Start Jackpot Round, settle, add-to-pot flows | Medium — writes state |
| **5 — V2 sunset** | Hide/remove V2 UI paths; V2 code kept but unused or behind flag | Higher — validate first |

**Per phase:** run `./gradlew test` + `./gradlew assembleDebug`; manual check against V3 scenarios doc.

---

## 9. Do Not Do Yet

| Item | Reason |
|---|---|
| Room database schema | Wait until V3 models and in-memory flow are stable |
| Delete / refund jackpot logic | Open question in V3 doc |
| Manual custom winner shares | Out of scope |
| Backend / cloud / login | Not MVP |
| Merge `feature/main-calculation-logic-v1` into `main` | Defer until V3 direction locked in code |
| Delete V2 files | Keep as reference and fallback until Phase 5 |
| Change app UI in planning step | This document only |

---

## 10. Open Items Carried from V3

Resolve before or during Phase 4:

1. Which portion of no-winner money enters Main Jackpot Pot (V3 scenarios assume full pot).
2. Whether local-only money ever enters Main Jackpot Pot on normal rounds.
3. Refund/delete behavior for pot and catch-up history.
4. Multiple Jackpot Rounds in one game (V3 lock assumes one active at a time).
5. One-time data migration from V2 chains to V3 pot + history (if existing user data matters).

---

## Document History

| Date | Change |
|---|---|
| 2026-06-21 | Initial migration plan (planning only) |
