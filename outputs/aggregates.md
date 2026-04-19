# Experiment aggregates

## Objective value: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| LS_Steepest_Vertex_Random | -9507.98 (-19806 - -1886) | -713.98 (-7702 - 5415) |
| LS_Steepest_Vertex_Heuristic | 6318.76 (5468 - 7318) | 18170.30 (16863 - 19560) |
| LS_Steepest_Edge_Random | 3472.60 (568 - 4879) | 12902.78 (11265 - 14169) |
| LS_Steepest_Edge_Heuristic | 6352.37 (5343 - 7318) | 18449.09 (17278 - 19410) |
| LS_Greedy_Vertex_Random | -7587.31 (-13791 - -2303) | 2316.76 (-2717 - 7756) |
| LS_Greedy_Vertex_Heuristic | 6296.76 (5343 - 7385) | 18168.26 (16863 - 19331) |
| LS_Greedy_Edge_Random | 3705.24 (1307 - 5496) | 12912.27 (10085 - 14365) |
| LS_Greedy_Edge_Heuristic | 6311.37 (5343 - 7385) | 18377.16 (17476 - 19455) |
| RandomWalk | -126898.81 (-138616 - -113304) | -119210.30 (-128252 - -108319) |


## Path length (totalDistance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| LS_Steepest_Vertex_Random | 32744.20 (25547 - 42232) | 33399.72 (26834 - 40921) |
| LS_Steepest_Vertex_Heuristic | 26528.25 (24669 - 28119) | 27241.59 (24974 - 28871) |
| LS_Steepest_Edge_Random | 19474.55 (17611 - 21883) | 19307.65 (17747 - 21244) |
| LS_Steepest_Edge_Heuristic | 26509.35 (24669 - 28119) | 26981.86 (25201 - 28692) |
| LS_Greedy_Vertex_Random | 29824.88 (24941 - 36740) | 29660.60 (24702 - 35312) |
| LS_Greedy_Vertex_Heuristic | 26573.10 (24843 - 27840) | 27350.71 (25397 - 29056) |
| LS_Greedy_Edge_Random | 18788.59 (16530 - 21069) | 19157.04 (16874 - 22175) |
| LS_Greedy_Edge_Heuristic | 26542.03 (24753 - 28119) | 27147.36 (25201 - 28872) |
| RandomWalk | 144622.14 (130408 - 156346) | 143531.18 (131425 - 153456) |


## Path length after phase I (phase1Distance): average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| LS_Steepest_Vertex_Random | 32744.20 (25547 - 42232) | 33399.72 (26834 - 40921) |
| LS_Steepest_Vertex_Heuristic | 26528.25 (24669 - 28119) | 27241.59 (24974 - 28871) |
| LS_Steepest_Edge_Random | 19474.55 (17611 - 21883) | 19307.65 (17747 - 21244) |
| LS_Steepest_Edge_Heuristic | 26509.35 (24669 - 28119) | 26981.86 (25201 - 28692) |
| LS_Greedy_Vertex_Random | 29824.88 (24941 - 36740) | 29660.60 (24702 - 35312) |
| LS_Greedy_Vertex_Heuristic | 26573.10 (24843 - 27840) | 27350.71 (25397 - 29056) |
| LS_Greedy_Edge_Random | 18788.59 (16530 - 21069) | 19157.04 (16874 - 22175) |
| LS_Greedy_Edge_Heuristic | 26542.03 (24753 - 28119) | 27147.36 (25201 - 28872) |
| RandomWalk | 144622.14 (130408 - 156346) | 143531.18 (131425 - 153456) |


## Time [ms]: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| LS_Steepest_Vertex_Random | 30.61ms (24 - 72) | 37.78ms (29 - 55) |
| LS_Steepest_Vertex_Heuristic | 5.93ms (4 - 21) | 5.72ms (3 - 10) |
| LS_Steepest_Edge_Random | 28.32ms (24 - 62) | 28.02ms (24 - 36) |
| LS_Steepest_Edge_Heuristic | 4.95ms (4 - 6) | 6.00ms (4 - 9) |
| LS_Greedy_Vertex_Random | 68.09ms (57 - 99) | 67.60ms (58 - 82) |
| LS_Greedy_Vertex_Heuristic | 5.40ms (4 - 7) | 5.87ms (4 - 9) |
| LS_Greedy_Edge_Random | 66.22ms (54 - 75) | 66.82ms (59 - 76) |
| LS_Greedy_Edge_Heuristic | 6.10ms (4 - 10) | 6.39ms (4 - 10) |
| RandomWalk | 68.27ms (68 - 69) | 67.22ms (67 - 68) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
