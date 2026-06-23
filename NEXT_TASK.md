# Next Task

## Current Goal
Implement `previewSplitCarryOverSummary` OR wire repository settlement to split engine.

## Verify Next
- FINISHED game → `calculateSplit` returns Resolved (even with no winners)
- Mixed local + jackpot winners get independent equal shares
- `previewSplitPayouts` maps Resolved → `TippGroupSplitPayouts`
- Legacy `calculate()` unchanged
