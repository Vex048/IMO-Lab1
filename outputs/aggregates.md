# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 6196.57 (5294 - 7360) | 18036.20 (17209 - 19401) |
| LS_Steepest_Edge_Random | 6399.99 (4694 - 7612) | 17984.66 (16193 - 19480) |
| LS_Steepest_Edge_Random_Candidate | 6366.36 (4473 - 7413) | 18003.92 (16167 - 19189) |
| LS_Steepest_Edge_Random_LM | 6276.03 (4827 - 7616) | 17935.40 (16045 - 19347) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 26638.68 (24951 - 28130) | 27298.91 (25210 - 28570) |
| LS_Steepest_Edge_Random | 21833.55 (17847 - 25192) | 24201.53 (21481 - 26807) |
| LS_Steepest_Edge_Random_Candidate | 22097.07 (18428 - 26122) | 23940.38 (21019 - 26183) |
| LS_Steepest_Edge_Random_LM | 23646.41 (19766 - 26596) | 25296.51 (22284 - 27869) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 32401.99 (31454 - 33411) | 32687.54 (31646 - 33532) |
| LS_Steepest_Edge_Random | 21833.55 (17847 - 25192) | 24201.53 (21481 - 26807) |
| LS_Steepest_Edge_Random_Candidate | 22097.07 (18428 - 26122) | 23940.38 (21019 - 26183) |
| LS_Steepest_Edge_Random_LM | 23646.41 (19766 - 26596) | 25296.51 (22284 - 27869) |


## Time [ms]: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| RegretCycle | 4.32ms (3 - 15) | 3.89ms (3 - 6) |
| LS_Steepest_Edge_Random | 49.32ms (40 - 97) | 51.81ms (46 - 58) |
| LS_Steepest_Edge_Random_Candidate | 8.10ms (5 - 89) | 6.91ms (5 - 9) |
| LS_Steepest_Edge_Random_LM | 4.29ms (2 - 24) | 3.31ms (2 - 5) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
