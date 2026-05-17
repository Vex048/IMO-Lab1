# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 7769.75 (7561 - 8055) | 19401.95 (19155 - 19765) |
| ILS | 8196.40 (7903 - 8666) | 19880.40 (19304 - 20203) |
| LNS | 8167.60 (7605 - 8529) | 19753.85 (19288 - 19995) |
| LNSa | 6357.85 (5177 - 7616) | 17760.80 (17163 - 18439) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 22461.75 (20611 - 24093) | 24097.60 (22508 - 25921) |
| ILS | 22955.55 (21015 - 24240) | 23867.50 (22520 - 24853) |
| LNS | 24152.50 (23020 - 25001) | 24912.95 (23872 - 26521) |
| LNSa | 23575.50 (20728 - 26156) | 25946.75 (23307 - 27869) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 22461.75 (20611 - 24093) | 24097.60 (22508 - 25921) |
| ILS | 22955.55 (21015 - 24240) | 23867.50 (22520 - 24853) |
| LNS | 24152.50 (23020 - 25001) | 24912.95 (23872 - 26521) |
| LNSa | 23575.50 (20728 - 26156) | 25946.75 (23307 - 27869) |


## Iterations/perturbations: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 200.00 (200 - 200) | 200.00 (200 - 200) |
| ILS | 1336.00 (1261 - 1392) | 1390.80 (1354 - 1427) |
| LNS | 255.20 (231 - 274) | 274.30 (248 - 317) |
| LNSa | 343.85 (299 - 376) | 394.00 (326 - 440) |


## Time [ms]: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| MSLS | 657.85ms (639 - 801) | 657.55ms (647 - 678) |
| ILS | 657.00ms (657 - 657) | 657.00ms (657 - 657) |
| LNS | 657.80ms (657 - 660) | 657.75ms (657 - 659) |
| LNSa | 657.55ms (657 - 659) | 657.15ms (657 - 658) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
