# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 6196.57 (5294 - 7360) | 18036.20 (17209 - 19401) |
| LS_Steepest_Baseline | 3564.42 (1595 - 5411) | 12715.50 (10425 - 14586) |
| LS_Steepest_MoveList | 3561.30 (1760 - 5314) | 12761.28 (9982 - 14932) |
| LS_Steepest_Candidate_k10 | 3299.77 (856 - 5164) | 12927.05 (10039 - 14992) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 26638.68 (24951 - 28130) | 27298.91 (25210 - 28570) |
| LS_Steepest_Baseline | 19437.39 (17390 - 21342) | 19436.68 (17787 - 21466) |
| LS_Steepest_MoveList | 18602.77 (16577 - 20192) | 18809.45 (16990 - 21456) |
| LS_Steepest_Candidate_k10 | 19658.11 (17527 - 22531) | 19252.15 (17587 - 21457) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 32401.99 (31454 - 33411) | 32687.54 (31646 - 33532) |
| LS_Steepest_Baseline | 19437.39 (17390 - 21342) | 19436.68 (17787 - 21466) |
| LS_Steepest_MoveList | 18602.77 (16577 - 20192) | 18809.45 (16990 - 21456) |
| LS_Steepest_Candidate_k10 | 19658.11 (17527 - 22531) | 19252.15 (17587 - 21457) |


## Time [ms]: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 0.00ms (0 - 0) | 0.00ms (0 - 0) |
| LS_Steepest_Baseline | 310.72ms (274 - 376) | 309.73ms (267 - 355) |
| LS_Steepest_MoveList | 1164.06ms (747 - 1717) | 1147.72ms (677 - 1850) |
| LS_Steepest_Candidate_k10 | 47.65ms (40 - 90) | 47.74ms (43 - 54) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
