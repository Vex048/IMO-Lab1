# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 6196.57 (5294 - 7360) | 18036.20 (17209 - 19401) |
| LS_Steepest_Edge_Random | 3564.42 (1595 - 5411) | 12715.50 (10425 - 14586) |
| LS_Steepest_Edge_Random_Candidate | 3472.93 (1478 - 5509) | 12914.51 (10720 - 14715) |
| LS_Steepest_Edge_Random_LM | 3361.50 (1784 - 5489) | 12701.23 (10614 - 14642) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 26638.68 (24951 - 28130) | 27298.91 (25210 - 28570) |
| LS_Steepest_Edge_Random | 19437.39 (17390 - 21342) | 19436.68 (17787 - 21466) |
| LS_Steepest_Edge_Random_Candidate | 19440.84 (17228 - 21457) | 19194.45 (16642 - 21318) |
| LS_Steepest_Edge_Random_LM | 19784.77 (17452 - 21922) | 19641.41 (17886 - 21248) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 32401.99 (31454 - 33411) | 32687.54 (31646 - 33532) |
| LS_Steepest_Edge_Random | 19437.39 (17390 - 21342) | 19436.68 (17787 - 21466) |
| LS_Steepest_Edge_Random_Candidate | 19440.84 (17228 - 21457) | 19194.45 (16642 - 21318) |
| LS_Steepest_Edge_Random_LM | 19784.77 (17452 - 21922) | 19641.41 (17886 - 21248) |


## Time [ms]: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 4.43ms (3 - 15) | 4.13ms (3 - 6) |
| LS_Steepest_Edge_Random | 21.28ms (17 - 50) | 20.45ms (17 - 24) |
| LS_Steepest_Edge_Random_Candidate | 9.24ms (6 - 43) | 9.22ms (7 - 33) |
| LS_Steepest_Edge_Random_LM | 3.33ms (2 - 21) | 2.70ms (2 - 4) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
