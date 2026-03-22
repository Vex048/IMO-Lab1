# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| Random | -147707.64 (-311268 - 233) | -139822.85 (-286852 - 463) |
| NN_Distance | 2562.12 (-2121 - 5971) | 13480.92 (10547 - 16455) |
| NN_Cost | -866.55 (-3899 - 2712) | 5788.22 (2251 - 9016) |
| GreedyCycle_Distance | 3484.70 (1894 - 4368) | 13676.68 (12527 - 15375) |
| GreedyCycle_Objective | 3678.74 (2936 - 4506) | 15043.74 (14152 - 16106) |
| RegretCycle | 6215.08 (5294 - 7360) | 18076.65 (16668 - 19486) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| Random | 165215.78 (0 - 345142) | 163864.26 (0 - 334236) |
| NN_Distance | 29560.22 (25951 - 34636) | 32263.93 (27996 - 35922) |
| NN_Cost | 31647.94 (28105 - 35285) | 38518.92 (34284 - 42438) |
| GreedyCycle_Distance | 28019.83 (26760 - 29992) | 30380.99 (28662 - 32406) |
| GreedyCycle_Objective | 28309.72 (27128 - 29934) | 29441.36 (27956 - 30766) |
| RegretCycle | 26670.48 (24846 - 28130) | 27291.28 (24955 - 29066) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| Random | 165215.78 (0 - 345142) | 163864.26 (0 - 334236) |
| NN_Distance | 39059.13 (36261 - 43753) | 38748.59 (36320 - 43276) |
| NN_Cost | 47496.68 (45854 - 49720) | 51287.86 (47096 - 55231) |
| GreedyCycle_Distance | 36875.57 (36229 - 37787) | 38417.60 (36935 - 39501) |
| GreedyCycle_Objective | 36216.40 (35422 - 37026) | 36670.90 (35995 - 37502) |
| RegretCycle | 32376.50 (31454 - 33411) | 32684.93 (31646 - 33532) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
