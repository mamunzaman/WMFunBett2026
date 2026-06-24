# WM Fun Bett 2026
# Jackpot Rules V3
## (Main Jackpot Pot Concept)

**Status:** Discussion / design only — not implemented.  
**Supersedes (proposed):** [Jackpot Rules V2](JACKPOT_RULES_V2.md)  
**V2 status:** Unchanged. V2 remains the authoritative reference for current app behavior and historical record.

---

## Purpose

This document proposes a replacement jackpot model for WM Fun Bett 2026.

V3 replaces **per-Tipp-Group jackpot chains** (V2) with a **single Main Jackpot Pot** shared across the tournament round.

This is a design discussion only. No app code, schema, or tests are changed by this document.

---

## Hierarchy (unchanged)

```
Round
 └── Day
      └── Game
           └── Tipp Group
                └── Entry
```

V3 does **not** change navigation, screens, or data hierarchy. Only jackpot **behavior** and **accounting** are proposed to change.

---

## Core Idea

| V2 (current) | V3 (proposed) |
|---|---|
| Separate jackpot chain per Tipp Group time scope | One **Main Jackpot Pot** for the round |
| Automatic chain carry per scope | Organizer manually marks a **Jackpot Round** on a chosen Tipp Group |
| Catch-up tied to missed chain rounds per scope | Catch-up tied to **missed source Tipp Groups** in catch-up history |
| Multiple active chains possible | One pot, one history |

---

## Normal Play

By default, **all Tipp Groups are normal (local) Tipp Groups**.

Supported Tipp Group types (unchanged):

- 1st Half Time
- 2nd Half Time
- Full Time
- Full Time + Penalty Shootout

Players participate with **local entries** unless they join a **Jackpot Round** (see below).

There is no automatic jackpot qualification on ordinary Tipp Groups in V3.

---

## No Winner (Normal Tipp Group)

When a **normal** Tipp Group finishes with **no winner**:

- Its **eligible money** may be added to the **Main Jackpot Pot**.
- The Tipp Group is recorded in **catch-up history** (see below).

### Source contribution tracking

Each contribution to the Main Jackpot Pot should record:

| Field | Description |
|---|---|
| Source game | Game where the no-winner Tipp Group occurred |
| Source Tipp Group | Which Tipp Group (type + id) contributed |
| Source entry amount | The Tipp Group’s configured entry amount (e.g. €10) |
| Source contributed amount | Actual money moved into the Main Jackpot Pot from that round |

> **Design note:** The relationship between *entry amount* and *contributed amount* is an open question (see Open Questions).

---

## Main Jackpot Pot

The round maintains a single **Main Jackpot Pot** with three related concepts:

### 1. Current jackpot amount

The total money currently held in the Main Jackpot Pot (sum of all contributions not yet paid out).

### 2. Jackpot history

A ledger of money **into** the pot:

- Which game / Tipp Group contributed
- How much was contributed
- When / why (e.g. no winner on normal round, no winner on jackpot round, **catch-up payment on Jackpot Round join**)

### 3. Required catch-up history

A list of **source Tipp Groups** that created jackpot obligation for late joiners.

Each record stores:

- Which Tipp Group created the jackpot entry (game + Tipp Group)
- **Entry amount** for that Tipp Group (the buy-in for that round)

### Critical rule: catch-up is NOT based on jackpot money amount

Catch-up is calculated from **missed source Tipp Group entry amounts**, not from how much money accumulated in the Main Jackpot Pot.

**Example**

| Game | Tipp Group | Entry amount | Result |
|---|---|---|---|
| Game 1 | Full Time | €10 | No winner → added to catch-up history |
| Game 2 | Half Time | €10 | No winner → added to catch-up history |
| Game 3 | Full Time | €10 | Winner → not in catch-up history |
| Game 4 | Full Time | €5 | No winner → added to catch-up history |

**Catch-up history entry amounts:** €10 + €10 + €5 = **€25**

A player who missed all three source rounds pays **€25 catch-up**, regardless of whether the Main Jackpot Pot holds €25, €50, or another total.

---

## Jackpot Round (organizer selection)

The organizer may **manually** designate a future Tipp Group as:

> **Use Main Jackpot Pot**

That Tipp Group becomes a **Jackpot Round**.

### Rules

- Only the **selected** Tipp Group in that game is a Jackpot Round.
- **All other Tipp Groups** in the same game (and elsewhere) remain **local**.
- Players who want jackpot eligibility in that round must **join jackpot** on that Tipp Group.
- Local-only entries on a Jackpot Round do **not** compete for the Main Jackpot Pot.

**Example**

```
Game 8
 ├── Half Time Tipp        → local (normal)
 └── Full Time Tipp        → Jackpot Round (Main Jackpot Pot)
```

---

## Joining a Jackpot Round

### Payment formula

```
totalDue = currentTippAmount + catchUp
```

Where:

```
catchUp = sum(entryAmount for each source Tipp Group in catch-up history
              that this player has NOT already joined as jackpot-qualified)
```

Catch-up is **person-specific**: if the player already joined jackpot on a prior source Tipp Group, that group is **not** charged again.

### Examples

| Scenario | Current Tipp | Catch-up | Total |
|---|---|---|---|
| Player joined all previous source rounds as jackpot | €10 | €0 | €10 |
| Player missed one €10 source round | €10 | €10 | €20 |
| Player missed €10 + €10 + €5 source rounds | €10 | €25 | €35 |

> **Alignment with V2:** Person-specific catch-up and “already joined = no double charge” mirror V2 late-join rules, but the **set of source rounds** is defined by catch-up history (global pot) rather than per-scope chains.

### Final decision: catch-up money → Main Jackpot Pot

**Approved:** Catch-up money paid during a Jackpot Round is added **directly** to the Main Jackpot Pot.

| Principle | Rule |
|---|---|
| Not local Tipp money | Catch-up is **not** part of the current Jackpot Round pot. |
| Purpose | Catch-up exists only because the player wants **jackpot eligibility** for missed source rounds. |
| Immediate effect | Catch-up **increases Main Jackpot Pot** as soon as it is paid (at join time). |
| Jackpot winner | Catch-up is **included** in the Main Jackpot Pot payout when the Jackpot Round has winner(s). |
| No jackpot winner | Catch-up **remains** in the Main Jackpot Pot (already there; not returned). |

The **current Tipp amount** still funds the **current Jackpot Round pot** only. Catch-up and current entry are separate money paths.

---

## Jackpot Winner

If a **Jackpot Round** finishes with at least one **jackpot-qualified correct winner**:

1. **Pay out** the full **Main Jackpot Pot** (including all catch-up paid during this Jackpot Round) to eligible winner(s) (split rule TBD — likely equal split among jackpot-qualified correct winners).
2. **Clear** jackpot history (contributions ledger).
3. **Reset** catch-up history.
4. Set **Main Jackpot Pot = €0**.

The current Tipp pot for that Jackpot Round is settled separately (same as normal: all correct winners share the current round stakes).

---

## No Jackpot Winner

If a **Jackpot Round** finishes with **no jackpot-qualified winner**:

1. The **current jackpot round contribution** (eligible money from this Jackpot Round) is **added** to the Main Jackpot Pot.
2. This Tipp Group is **added** to **catch-up history** (entry amount recorded).
3. The Main Jackpot Pot **continues** — not paid out, not reset.

Jackpot-qualified players who were incorrect do not win the pot; the pot grows and late joiners on future Jackpot Rounds must catch up on this round’s entry amount.

Catch-up paid at join time on this Jackpot Round is **already** in the Main Jackpot Pot (see Final decision above); on no winner, only the **current Jackpot Round pot** is added to the Main Jackpot Pot.

---

## Money Flow Summary

| Money Source | Destination |
|---|---|
| Normal Tipp Group with winner | Paid to winner(s) |
| Normal Tipp Group with no winner | Main Jackpot Pot |
| Jackpot Round current entry money | Current Jackpot Round pot |
| Jackpot Round catch-up money | Main Jackpot Pot (immediately at join) |
| Jackpot Round with winner | Current Jackpot Round pot **and** Main Jackpot Pot paid out to jackpot-qualified winner(s) |
| Jackpot Round with no winner | Current Jackpot Round pot added to Main Jackpot Pot |

---

## V3 Test Scenarios

Reference scenarios for future Kotlin unit tests and manual verification. Numbers assume **all stakes** from no-winner normal rounds enter the Main Jackpot Pot.

### Scenario 1 — Single no-winner Tipp Round

| Item | Value |
|---|---|
| Game | Game 1 |
| Tipp Groups | 1 (normal) |
| Players | 3 × €10 |
| Result | No winner |

| Expected | Value |
|---|---|
| Main Jackpot Pot | €30 |
| Catch-up history (source entry amount) | €10 |

---

### Scenario 2 — Multiple games contributing

| Game | Tipp Group | Round pot | Source entry | Result |
|---|---|---|---|---|
| Game 1 | Half Time | €30 | €10 | No winner |
| Game 2 | Full Time | €20 | €10 | No winner |
| Game 3 | — | — | — | (skipped / had winner — not in history) |
| Game 4 | Penalty | €40 | €10 | No winner |

| Expected | Value |
|---|---|
| Main Jackpot Pot | €90 (€30 + €20 + €40) |
| Catch-up history entry amounts | €10 + €10 + €10 = **€30** |
| Full catch-up for new player (missed all 3) | **€30** |

---

### Scenario 3 — New player joins Jackpot Round

**Setup:** After Scenario 2 — Main Jackpot Pot €90, catch-up history €30, current Jackpot Round entry €10.

| Payment component | Amount | Destination |
|---|---|---|
| Catch-up (missed all 3 source rounds) | €30 | Main Jackpot Pot (immediately) |
| Current round entry | €10 | Current Jackpot Round pot |
| **Total due** | **€40** | — |

| Expected after join | Value |
|---|---|
| Main Jackpot Pot | €120 (€90 + €30 catch-up) |
| Current Jackpot Round pot | +€10 (this player’s current stake) |

---

### Scenario 4 — Existing player joins Jackpot Round

**Setup:** Same catch-up history as Scenario 2 (3 source rounds × €10). Player already joined jackpot on 2 of 3 source rounds.

| Item | Value |
|---|---|
| Missed source rounds | 1 × €10 |
| Catch-up | €10 |
| Current round entry | €10 |
| **Total due** | **€20** |

| Expected | Value |
|---|---|
| €10 catch-up | Main Jackpot Pot |
| €10 current | Current Jackpot Round pot |

---

### Scenario 5 — Jackpot winner

**Setup:** Main Jackpot Pot after catch-up payments = €130. Current Jackpot Round pot = €30 (e.g. 3 jackpot players × €10). 2 correct jackpot-qualified winners.

| Pot | Total | Winners | Payout each |
|---|---|---|---|
| Current Jackpot Round pot | €30 | 2 | €15 |
| Main Jackpot Pot | €130 | 2 | €65 |

| Expected after settlement | Value |
|---|---|
| Main Jackpot Pot | €0 |
| Jackpot history | Cleared |
| Catch-up history | Cleared |
| Current round pot | Paid out (€15 each to 2 winners) |

---

### Scenario 6 — Jackpot no winner

**Setup:** Main Jackpot Pot before round €90. During join phase, catch-up payments total €30 (added to Main Jackpot Pot). Current Jackpot Round pot €30. No jackpot-qualified winner.

| Stage | Main Jackpot Pot |
|---|---|
| Before round | €90 |
| After catch-up joins (+€30) | €120 |
| After no winner (+€30 current round pot) | **€150** |

| Expected after settlement | Value |
|---|---|
| Main Jackpot Pot | €150 |
| Catch-up history | +€10 (this Jackpot Round’s source entry amount) |
| Pot paid out | No — jackpot continues |

---

### Scenario 7 — One active Jackpot Round lock

| State | Rule |
|---|---|
| Jackpot Round started | Game 8 Full Time marked “Use Main Jackpot Pot”; entries open / result pending |
| Before result entered | **Cannot** start another Jackpot Round in Game 8, Game 9, or Game 10 |
| After settlement | Jackpot Round slot available again; organizer may mark a new Jackpot Round |

| Expected | Value |
|---|---|
| Concurrent Jackpot Rounds | **0** (only one active at a time) |
| Lock scope | Entire round until current Jackpot Round is settled |

---

## Comparison: V2 vs V3

| Topic | V2 | V3 |
|---|---|---|
| Pot structure | Per time-scope chain | Single Main Jackpot Pot |
| When jackpot activates | Any Tipp Group with jackpot entries | Organizer marks specific Tipp Group as Jackpot Round |
| Carry trigger | No jackpot-qualified winner on chain | No winner on normal round **or** no jackpot winner on Jackpot Round |
| Catch-up basis | Missed chain rounds (per scope) | Missed source Tipp Groups (catch-up history) |
| Catch-up amount | Sum of missed round entry amounts | Sum of missed source entry amounts (same idea, different source list) |
| Active chains | One per time scope | None — one pot |
| Organizer control | Implicit (join jackpot on any group) | Explicit (pick Jackpot Round) |

---

## Advantages over V2

- **One jackpot pot** — easier to explain: “the jackpot is €X”.
- **Easier for friends** — no need to understand per-scope chains (Half Time vs Full Time carry).
- **No multiple active chains** — eliminates parallel carries and scope-specific incoming jackpot display.
- **No chain-per-Tipp-Group complexity** — settlement and catch-up reference one global history.
- **Manual organizer control** — jackpot runs when the group chooses, not on every jackpot-enabled Tipp Group.

---

## Relationship to V2 (reference)

[V2](JACKPOT_RULES_V2.md) defines:

- Dual pools (current pot + jackpot pot) per Tipp Group round
- Per-scope incoming jackpot chain
- Person-specific catch-up for missed jackpot-chain rounds
- Local money returned on no-winner mixed rounds (not added to jackpot)

V3 **intentionally diverges** on several points (single pot, organizer-selected rounds, catch-up history from no-winner normal rounds). V2 code and tests remain valid for the current app until a future migration decision.

---

## Open Questions

1. **Which portion of no-winner Tipp Group money enters the Main Jackpot Pot?**  
   All stakes? Only jackpot-qualified stakes? Only the “current pot” portion? Excluding local-only money?

2. **Should local-only money enter the jackpot?**  
   V2 explicitly excludes local money from carry. Should V3 add only jackpot stakes, only winner-eligible pool, or entire pot when everyone is local?

3. **How should refunds/deletes behave?**  
   If an entry or Tipp Group is deleted after contributing to the pot or catch-up history, should history be recalculated, frozen, or adjusted manually?

4. **Can multiple jackpot rounds exist in the same game?**  
   This design assumes one Jackpot Round per selection event; same game could have Half Time local + Full Time jackpot. Can both Half Time and Full Time be Jackpot Rounds in one game?

5. **Migration path from V2 to V3**  
   How to map existing per-scope carries, open chains, and person-specific catch-up into Main Jackpot Pot + catch-up history? One-time conversion vs clean slate vs parallel run?

---

## Out of scope (this document)

- Implementation plan, Room schema, UI mockups
- Payout split formulas for multi-winner Jackpot Rounds (assume equal split unless decided later)
- Kasse / accounting integration
- Changes to local Tipp Group winner logic

---

## Document history

| Date | Change |
|---|---|
| 2026-06-21 | Initial V3 design draft (discussion only) |
| 2026-06-21 | Final decision: Jackpot Round catch-up → Main Jackpot Pot immediately; money flow table |
| 2026-06-21 | V3 Test Scenarios 1–7 (reference for future implementation tests) |
