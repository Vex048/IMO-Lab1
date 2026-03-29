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
| RandomWalk | -125966.21 (-140838 - -113304) | -117936.56 (-124463 - -108242) |


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
| RandomWalk | 143712.13 (130408 - 159058) | 142258.42 (131425 - 148856) |


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
| RandomWalk | 143712.13 (130408 - 159058) | 142258.42 (131425 - 148856) |


## Time [ms]: average (min - max)

| Method | TSPA | TSPB |
|---|---|---|
| LS_Steepest_Vertex_Random | 406.81ms (4 - 119) | 116.88ms (4 - 92) |
| LS_Steepest_Vertex_Heuristic | 127.12ms (4 - 92) | 5.25ms (4 - 14) |
| LS_Steepest_Edge_Random | 506.82ms (4 - 119) | 223.64ms (4 - 92) |
| LS_Steepest_Edge_Heuristic | 79.03ms (4 - 92) | 363.00ms (4 - 92) |
| LS_Greedy_Vertex_Random | 284.06ms (4 - 92) | 74.23ms (4 - 92) |
| LS_Greedy_Vertex_Heuristic | 121.86ms (4 - 92) | 368.23ms (4 - 92) |
| LS_Greedy_Edge_Random | 574.55ms (4 - 119) | 194.95ms (4 - 92) |
| LS_Greedy_Edge_Heuristic | 357.46ms (4 - 92) | 413.02ms (4 - 119) |
| RandomWalk | 480.02ms (4 - 119) | 352.06ms (4 - 92) |


## Notes
- `details_runs.csv` contains every single run and the complete tour.
- Full tour files are in `outputs/runs/<method>/<instance>/run_XXX.txt`.
- `best_worst_runs.csv` points to best and worst run per instance/method.
