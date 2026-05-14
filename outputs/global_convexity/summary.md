# Task 5 - global convexity results

Generated local optima per instance: 1000
Local optima generation: randomized starting solution followed by greedy local search with EDGE_SWAP neighborhood.
Good solution heuristic: ILS, time limit 2000 ms.

## Good ILS solutions

| Instance | Seed | Time [ms] | Objective | Reward | Distance | Nodes |
|---|---:|---:|---:|---:|---:|---:|
| TSPA | 21160515 | 2000 | 8380 | 30824 | 22444 | 149 |
| TSPB | 22160518 | 2000 | 20073 | 43379 | 23306 | 155 |

## Local optima summary

| Instance | Points | Avg objective | Min objective | Max objective | Avg nodes | Avg vertices to best | Avg edges to best | Avg vertices to others | Avg edges to others |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| TSPA | 1000 | 5876.357000 | 3814 | 7814 | 149.068000 | 131.790000 | 91.865000 | 131.086529 | 85.325365 |
| TSPB | 1000 | 17520.505000 | 15094 | 19303 | 161.787000 | 147.481000 | 106.423000 | 149.457309 | 97.951634 |

## Pearson correlations

| Instance | Compared with | Measure | Pearson r | Points |
|---|---|---|---:|---:|
| TSPA | good ILS solution | common vertices | 0.197507 | 1000 |
| TSPA | good ILS solution | common edges | 0.456601 | 1000 |
| TSPA | average local optimum | common vertices | 0.035162 | 1000 |
| TSPA | average local optimum | common edges | 0.413255 | 1000 |
| TSPB | good ILS solution | common vertices | 0.217234 | 1000 |
| TSPB | good ILS solution | common edges | 0.589503 | 1000 |
| TSPB | average local optimum | common vertices | 0.074387 | 1000 |
| TSPB | average local optimum | common edges | 0.639610 | 1000 |

The best ILS solution is not included as a plotted point and is not included in the correlation sample.
Plot x-axis: objective value of a greedy local optimum. Plot y-axis: selected similarity measure.
