# Python plotting scripts

Skrypty w tym katalogu generuja wykresy na podstawie wynikow z `outputs/`.

## Wymagania

- Python 3.10+
- `matplotlib`

Instalacja (opcjonalnie w venv projektu):

```bash
pip install -r scripts/python/requirements.txt
```

## Generowanie wykresow najlepszych tras

Skrypt: `scripts/python/plot_best_solutions.py`

Domyslnie czyta `outputs/best_worst_runs.csv` i generuje PNG dla wszystkich metod i instancji do `outputs/plots`.

Przyklady:

```bash
python scripts/python/plot_best_solutions.py
python scripts/python/plot_best_solutions.py --instance TSPA
python scripts/python/plot_best_solutions.py --method-contains Candidate
```

Najwazniejsze argumenty:

- `--best-worst-csv` (domyslnie `outputs/best_worst_runs.csv`)
- `--datasets-dir` (domyslnie `datasets`)
- `--output-dir` (domyslnie `outputs/plots`)
- `--instance` (`ALL`, `TSPA`, `TSPB`, ...)
- `--method-contains` (filtrowanie po nazwie metody)

