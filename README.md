# IMO-Lab1

## Co zostało przeniesione

Do architektury `src/` zostały dodane heurystyki przeniesione z modułu `tsp/`:
- `GreedyCycleDistanceHeuristic` (wariant GCa)
- `GreedyCycleObjectiveHeuristic` (wariant GCb)
- `RegretCycleHeuristic` (2-regret)
- `RandomHeuristic`

Dodatkowo dodano pomocniczą klasę `solution/CycleDeltas`, która udostępnia obliczenia delta (insert/remove) dla cyklu.

## Uruchomienie

Domyslny runner w `src/Main.java` uruchamia kampanie eksperymentow dla:
- instancji: `datasets/TSPA.csv`, `datasets/TSPB.csv`
- metod: `Random`, `NN_Distance`, `NN_Cost`, `GreedyCycle_Distance`, `GreedyCycle_Objective`, `RegretCycle`
- wielu powtorzen (`runsPerCombination`, domyslnie `50`)

Przykład kompilacji i uruchomienia:

```bash
cd /home/marcin/Studia/IMO/IMO-Lab1
rm -rf out
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out Main
```

Po uruchomieniu generowane sa pliki:
- `outputs/details_runs.csv` - wszystkie pojedyncze uruchomienia (w tym `phase1Distance` oraz pelna sciezka/tour)
- `outputs/aggregates.csv` - statystyki srednia/min/max dla kazdej pary instancja-metoda (w tym `avgPhase1Distance`)
- `outputs/aggregates.md` - gotowe tabele do sprawozdania (objective, final distance, phase I distance)
- `outputs/best_worst_runs.csv` - wskazanie najlepszego i najgorszego runu dla kazdej pary instancja-metoda
- `outputs/runs/<method>/<instance>/run_XXX.txt` - osobne pliki ze sciezka dla kazdego runu

W razie potrzeby zmien liczbe powtorzen, seed i liste metod/instancji bezposrednio w `src/Main.java`.

