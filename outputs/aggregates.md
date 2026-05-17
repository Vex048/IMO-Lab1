# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 7769.75 (7561 - 8055) | 19401.95 (19155 - 19765) |
| ILS | 8202.70 (7903 - 8666) | 19883.50 (19304 - 20203) |
| LNS | 8145.35 (7597 - 8529) | 19777.85 (19478 - 20014) |
| LNSa | 8031.65 (7294 - 8533) | 19643.35 (19362 - 20055) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 22461.75 (20611 - 24093) | 24097.60 (22508 - 25921) |
| ILS | 22953.95 (21015 - 24372) | 23855.25 (22275 - 24853) |
| LNS | 24065.45 (23020 - 25502) | 25027.30 (24150 - 26333) |
| LNSa | 24222.10 (23056 - 25587) | 25202.95 (24204 - 26054) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 22461.75 (20611 - 24093) | 24097.60 (22508 - 25921) |
| ILS | 22953.95 (21015 - 24372) | 23855.25 (22275 - 24853) |
| LNS | 24065.45 (23020 - 25502) | 25027.30 (24150 - 26333) |
| LNSa | 24222.10 (23056 - 25587) | 25202.95 (24204 - 26054) |


## Iterations/perturbations: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 200.00 (200 - 200) | 200.00 (200 - 200) |
| ILS | 1343.45 (1308 - 1414) | 1400.85 (1351 - 1442) |
| LNS | 293.00 (263 - 314) | 327.75 (301 - 364) |
| LNSa | 370.80 (328 - 390) | 400.10 (359 - 434) |


## Time [ms]: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 652.70ms (631 - 783) | 654.60ms (643 - 663) |
| ILS | 652.00ms (652 - 652) | 654.00ms (654 - 654) |
| LNS | 652.70ms (652 - 654) | 654.70ms (654 - 656) |
| LNSa | 652.35ms (652 - 653) | 654.40ms (654 - 656) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
