# Next Task

## Current Goal
Add Room database with entities matching the hierarchy (Round, Day, Game, TippGroup, Entry) and replace SampleData lookups.

## Verify Next
- Rounds tab → tap Bundesliga Round 1 → Saturday → Germany vs France → Correct Score → View Entries
- Alex 2:1 €10 and John 1:1 €5 visible on Entry List
- Back navigation works at every level
- Breadcrumbs update on each screen
- Bottom bar hidden on detail screens, visible on Rounds list
- Gold FAB visible on all hierarchy screens
- Build passes with `./gradlew assembleDebug`
