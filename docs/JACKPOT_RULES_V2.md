# Jackpot Rules V2

Authoritative rules for mixed **LOCAL_ONLY** and **JACKPOT** participants in one Tipp Group round.

**Status:** Final business logic approved (2026-06-21). `JackpotV2Calculator` implemented and unit-tested.

---

## Final Rules (Approved)

- Current Tipp pot is shared by **all** correct winners.
- Jackpot pot is shared only by correct **jackpot-qualified** winners.
- Local players never win jackpot money.
- Jackpot-qualified players can win **both** current pot and jackpot pot.
- Local money never carries automatically to jackpot.
- In a no-winner mixed round, local money is **returned/closed** (not added to jackpot).
- Jackpot participant money **carries forward** when there is no jackpot-qualified winner.
- Late jackpot join requires paying **all missed chain Tipp amounts** plus the **current Tipp amount**.

---

## 1. Current Tipp Pot

- Every participant in the current Tipp round pays the **current Tipp amount**.
- **LOCAL_ONLY** and **JACKPOT** entries both count toward the current pot.
- **All correct winners** share the current pot **equally**, regardless of participation type.

**Formula**

```
currentPot = sum(currentRoundAmount for all entries)
currentShare = currentPot / count(allCorrectWinners)
```

---

## 2. Jackpot Pot

- The jackpot pot is **carried forward** from previous Tipp rounds where there was **no jackpot-qualified winner**.
- Only **jackpot-qualified** participants who predicted correctly may win from this pot.
- Local-only winners **never** receive jackpot pot money.

**Formula (payout)**

```
jackpotShare = incomingJackpot / count(jackpotQualifiedCorrectWinners)
```

---

## 3. Local Participant (`LOCAL_ONLY`)

| Rule | Value |
|---|---|
| Pays | Current Tipp amount only |
| Can win | Current Tipp pot only |
| Cannot win | Jackpot pot |
| Catch-up | None |

---

## 4. Jackpot Participant (`JACKPOT`)

| Rule | Value |
|---|---|
| Pays | All **missed jackpot-chain Tipp amounts** + **current Tipp amount** |
| Qualification | Full catch-up must be paid at join time |
| Can win | Current Tipp pot **and** Jackpot pot (when correct) |

**Late join formula**

```
catchUp = sum(per-round buy-in for each missed jackpot-chain Tipp)
totalDue = catchUp + currentTippAmount
```

Example: missed chain Tipp 1 €5 + Tipp 2 €5 + current Tipp 3 €5 → **€15 total**.

---

## 5. Mixed Winners Example

**Setup**

- Previous jackpot: **€30**
- Current Tipp:
  - Ole — JACKPOT — €5
  - Thomas — JACKPOT — €5
  - Mamun — LOCAL_ONLY — €5

**Pots**

```
currentPot = €5 + €5 + €5 = €15
incomingJackpot = €30
```

**Result:** Ole + Mamun correct (Thomas wrong)

| Winner | Current share | Jackpot share | Total |
|---|---|---|---|
| Mamun (local) | €7.50 | — | **€7.50** |
| Ole (jackpot) | €7.50 | €30.00 | **€37.50** |

```
currentShare = €15 / 2 = €7.50
jackpotShare = €30 / 1 = €30.00   (Ole only jackpot-qualified winner)
```

**Carry after settlement:** €0 (jackpot claimed by Ole)

---

## 6. Mixed No-Winner Example

**Setup**

- Previous jackpot: **€30**
- Current Tipp:
  - Ole — JACKPOT — €5
  - Thomas — JACKPOT — €5
  - Mamun — LOCAL_ONLY — €5

**Result:** Nobody correct

| Pool | Outcome |
|---|---|
| Mamun local €5 | Returned / closed locally (does **not** enter jackpot) |
| Ole + Thomas jackpot stakes €10 | Added to jackpot carry |
| New jackpot carry | **€40** (= €30 previous + €10 current jackpot participant money) |

```
newJackpot = previousJackpot + currentJackpotParticipantMoney
           = €30 + €10
           = €40
```

Local-only money is **never** automatically added to the jackpot carry.

---

## Final Approved Scenarios

All amounts use **current Tipp amount = €5** per participant unless noted.

### Scenario 1 — Jackpot starts

**Tipp 1**

| Player | Type | Stake |
|---|---|---|
| Ole | JACKPOT | €5 |
| Thomas | JACKPOT | €5 |
| Bello | JACKPOT | €5 |

**Result:** No winner

**Jackpot carry:** **€15**

---

### Scenario 2 — Jackpot grows

**Tipp 2** (after Tipp 1 carry €15)

| Player | Type | Stake |
|---|---|---|
| Ole | JACKPOT | €5 |
| Thomas | JACKPOT | €5 |
| Bello | JACKPOT | €5 |

**Result:** No winner

**Jackpot carry:** €15 + €15 = **€30**

---

### Scenario 3 — Local + Jackpot winners

**Previous jackpot:** €30

**Current Tipp**

| Player | Type | Stake |
|---|---|---|
| Ole | JACKPOT | €5 |
| Thomas | JACKPOT | €5 |
| Mamun | LOCAL_ONLY | €5 |

**Current pot:** €15  
**Winners:** Ole + Mamun (Thomas wrong)

**Current pot split:** €15 ÷ 2 = **€7.50 each**

**Jackpot:** €30 → Ole only (sole jackpot-qualified winner)

| Player | Current | Jackpot | **Total** |
|---|---|---|---|
| Ole | €7.50 | €30.00 | **€37.50** |
| Mamun | €7.50 | — | **€7.50** |

**Jackpot carry after:** €0

---

### Scenario 4 — Local winner only

**Previous jackpot:** €30  
**Current pot:** €15 (same 3-player mix as Scenario 3)

**Winner:** Mamun (LOCAL_ONLY) only

| Player | Payout |
|---|---|
| Mamun | **€15** (full current pot) |

**Jackpot carry after:** **€30** (unchanged — no jackpot-qualified winner)

---

### Scenario 5 — Jackpot winner only

**Previous jackpot:** €30  
**Current pot:** €15

**Winner:** Ole (JACKPOT) only

| Component | Amount |
|---|---|
| Current pot | €15 |
| Jackpot | €30 |
| **Ole total** | **€45** |

**Jackpot carry after:** **€0**

---

### Scenario 6 — No winner in mixed round

**Previous jackpot:** €30

**Current Tipp**

| Player | Type | Stake |
|---|---|---|
| Ole | JACKPOT | €5 |
| Thomas | JACKPOT | €5 |
| Mamun | LOCAL_ONLY | €5 |

**Result:** Nobody correct

| Pool | Outcome |
|---|---|
| Mamun local €5 | Returned / closed |
| Ole + Thomas jackpot stakes | €10 added to jackpot carry |

**New jackpot:** €30 + €10 = **€40**

---

### Scenario 7 — Late jackpot join

**Previous chain:** Tipp 1 €5 + Tipp 2 €5 (no jackpot winner yet)  
**Current Tipp 3 amount:** €5

| Join type | Pays | Reason |
|---|---|---|
| **Jackpot (late)** | **€15** | €5 + €5 catch-up + €5 current |
| **Local only** | **€5** | Current Tipp amount only |

---

## Carry Rules Summary

| Situation | Jackpot carry after round |
|---|---|
| Jackpot-qualified winner exists | **€0** (pot claimed) |
| Local-only winner(s), no jackpot winner | Previous jackpot **unchanged** |
| No winner | Previous jackpot + **current jackpot participant money only** |
| Local-only money | Never auto-merged into jackpot |

---

## Payout Summary

```
localWinnerPayout   = currentShare
jackpotWinnerPayout = currentShare + jackpotShare
```

---

## Out of Scope (this document)

- Kotlin calculator implementation
- UI / repository changes
- Room schema / persistence
- Friends, match edit/delete, hierarchy changes

Next step: implement calculator to match **Final Approved Scenarios** above.
