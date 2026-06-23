# Jackpot Rules V2

Authoritative rules for mixed **LOCAL_ONLY** and **JACKPOT** participants in one Tipp Group round.

**Status:** Documented only. Calculation engine not yet fully aligned to every V2 carry edge case.

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

## Out of Scope (V2 doc only)

- UI layout and settlement label text
- Room schema / persistence
- Friends, match edit/delete, hierarchy changes

Implementation must follow this document in a future calculation pass.
