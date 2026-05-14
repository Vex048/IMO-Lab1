# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 7769.75 (7561 - 8055) | 19401.95 (19155 - 19765) |
| ILS | 8196.40 (7903 - 8666) | 19883.50 (19304 - 20203) |
| LNS | 8145.85 (7597 - 8529) | 19777.85 (19478 - 20014) |
| LNSa | 8031.65 (7294 - 8533) | 19646.50 (19362 - 20055) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 22461.75 (20611 - 24093) | 24097.60 (22508 - 25921) |
| ILS | 22955.55 (21015 - 24240) | 23855.25 (22275 - 24853) |
| LNS | 24067.50 (23020 - 25502) | 25027.30 (24150 - 26333) |
| LNSa | 24222.10 (23056 - 25587) | 25197.20 (24204 - 26054) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 22461.75 (20611 - 24093) | 24097.60 (22508 - 25921) |
| ILS | 22955.55 (21015 - 24240) | 23855.25 (22275 - 24853) |
| LNS | 24067.50 (23020 - 25502) | 25027.30 (24150 - 26333) |
| LNSa | 24222.10 (23056 - 25587) | 25197.20 (24204 - 26054) |


## Iterations/perturbations: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 200.00 (200 - 200) | 200.00 (200 - 200) |
| ILS | 1346.90 (1271 - 1402) | 1400.30 (1278 - 1470) |
| LNS | 296.85 (275 - 319) | 328.30 (302 - 363) |
| LNSa | 374.40 (334 - 397) | 420.50 (396 - 435) |


## Time [ms]: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 644.25ms (626 - 756) | 659.55ms (630 - 712) |
| ILS | 644.05ms (644 - 645) | 659.05ms (659 - 660) |
| LNS | 644.65ms (644 - 646) | 659.60ms (659 - 661) |
| LNSa | 644.55ms (644 - 646) | 659.35ms (659 - 660) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
