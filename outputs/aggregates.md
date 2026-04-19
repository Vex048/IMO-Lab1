# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| WeightedRegrestCycle | 6034.08 (4129 - 7208) | 18079.14 (16492 - 19013) |
| LS_Steepest_Edge_Random | 3564.42 (1595 - 5411) | 12715.50 (10425 - 14586) |
| LS_Steepest_Edge_Random_Candidate | 3472.93 (1478 - 5509) | 12914.51 (10720 - 14715) |
| LS_Steepest_Edge_Random_LM | 3361.50 (1784 - 5489) | 12701.23 (10614 - 14642) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| WeightedRegrestCycle | 26828.09 (24936 - 29119) | 27113.18 (25084 - 29324) |
| LS_Steepest_Edge_Random | 19437.39 (17390 - 21342) | 19436.68 (17787 - 21466) |
| LS_Steepest_Edge_Random_Candidate | 19440.84 (17228 - 21457) | 19194.45 (16642 - 21318) |
| LS_Steepest_Edge_Random_LM | 19784.77 (17452 - 21922) | 19641.41 (17886 - 21248) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| WeightedRegrestCycle | 32698.78 (31642 - 33738) | 33110.49 (32088 - 34817) |
| LS_Steepest_Edge_Random | 19437.39 (17390 - 21342) | 19436.68 (17787 - 21466) |
| LS_Steepest_Edge_Random_Candidate | 19440.84 (17228 - 21457) | 19194.45 (16642 - 21318) |
| LS_Steepest_Edge_Random_LM | 19784.77 (17452 - 21922) | 19641.41 (17886 - 21248) |


## Time [ms]: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| WeightedRegrestCycle | 4.19ms (3 - 17) | 4.53ms (3 - 7) |
| LS_Steepest_Edge_Random | 21.77ms (18 - 50) | 22.03ms (17 - 35) |
| LS_Steepest_Edge_Random_Candidate | 11.62ms (7 - 38) | 10.10ms (7 - 42) |
| LS_Steepest_Edge_Random_LM | 4.32ms (2 - 25) | 2.97ms (2 - 4) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
