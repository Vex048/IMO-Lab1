# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| Random | -147707.64 (-311268 - 233) | -140190.08 (-300916 - 100) |
| NN_Distance | 2562.12 (-2121 - 5971) | 13255.75 (10375 - 16455) |
| NN_Cost | -866.55 (-3899 - 2712) | 5717.60 (2251 - 9016) |
| GreedyCycle_Distance | 3484.70 (1894 - 4368) | 13609.83 (12527 - 15375) |
| GreedyCycle_Objective | 3678.74 (2936 - 4506) | 15108.36 (14181 - 16106) |
| RegretCycle | 6215.08 (5294 - 7360) | 18036.36 (16668 - 19486) |
| WeightedRegrestCycle | 5980.41 (4129 - 7208) | 18132.40 (17040 - 19080) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| Random | 165215.78 (0 - 345142) | 164284.01 (0 - 349075) |
| NN_Distance | 29560.22 (25951 - 34636) | 32509.98 (27996 - 35977) |
| NN_Cost | 31647.94 (28105 - 35285) | 38614.62 (34284 - 42438) |
| GreedyCycle_Distance | 28019.83 (26760 - 29992) | 30454.70 (28027 - 32406) |
| GreedyCycle_Objective | 28309.72 (27128 - 29934) | 29354.46 (27956 - 30766) |
| RegretCycle | 26670.48 (24846 - 28130) | 27311.93 (24955 - 29066) |
| WeightedRegrestCycle | 26860.84 (24936 - 29119) | 27040.21 (25084 - 29324) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| Random | 165215.78 (0 - 345142) | 164284.01 (0 - 349075) |
| NN_Distance | 39059.13 (36261 - 43753) | 38923.16 (36456 - 43455) |
| NN_Cost | 47496.68 (45854 - 49720) | 51107.80 (47096 - 53935) |
| GreedyCycle_Distance | 36875.57 (36229 - 37787) | 38483.37 (36935 - 39501) |
| GreedyCycle_Objective | 36216.40 (35422 - 37026) | 36627.27 (35973 - 37502) |
| RegretCycle | 32376.50 (31454 - 33411) | 32727.47 (31646 - 33532) |
| WeightedRegrestCycle | 32712.10 (31642 - 33738) | 33059.37 (32086 - 34817) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
