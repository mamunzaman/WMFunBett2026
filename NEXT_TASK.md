# Next Task

## Current Goal
Wire repository settlement to split engine OR Add Entry JACKPOT UI.

## Verify Next
- FINISHED + no winners → `carriedOut = jackpotTotalPot`, `local.closed` if localPot > 0
- JACKPOT winner → `carriedOut = 0`; local winner only does not reset jackpot carry
- Legacy `JackpotCarryOverSummary` / UI unchanged
