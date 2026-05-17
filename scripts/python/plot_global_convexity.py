# -*- coding: utf-8 -*-

import csv
import os
from collections import defaultdict

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt


DETAILS_CSV = "outputs/global_convexity/details.csv"
CORRELATIONS_CSV = "outputs/global_convexity/correlations.csv"
PLOTS_DIR = "outputs/global_convexity/plots"


def load_rows(path):
    with open(path, newline="") as csv_file:
        return list(csv.DictReader(csv_file))


PLOTS = [
    (
        "vertexSimilarityToBest",
        "Wspólne wierzchołki",
        "dobrego ILS",
        "vertices_to_best",
        ("best_solution", "vertices"),
    ),
    (
        "edgeSimilarityToBest",
        "Wspólne krawędzie",
        "dobrego ILS",
        "edges_to_best",
        ("best_solution", "edges"),
    ),
    (
        "avgVertexSimilarityToOthers",
        "Średnie wspólne wierzchołki",
        "innych optimów",
        "vertices_to_others",
        ("local_optima_average", "vertices"),
    ),
    (
        "avgEdgeSimilarityToOthers",
        "Średnie wspólne krawędzie",
        "innych optimów",
        "edges_to_others",
        ("local_optima_average", "edges"),
    ),
]


def load_correlations(path):
    if not os.path.exists(path):
        return {}

    correlations = {}
    for row in load_rows(path):
        key = (row["instance"], row["similarityScope"], row["similarityMeasure"])
        correlations[key] = float(row["pearsonCorrelation"])
    return correlations


def linear_fit(x, y):
    n = len(x)
    mean_x = sum(x) / n
    mean_y = sum(y) / n
    variance_x = sum((value - mean_x) ** 2 for value in x)
    if variance_x == 0:
        return None

    covariance = sum((x[i] - mean_x) * (y[i] - mean_y) for i in range(n))
    slope = covariance / variance_x
    intercept = mean_y - slope * mean_x
    return slope, intercept


def draw_scatter(ax, rows, y_column, y_label, title_scope, correlation):
    x = [int(row["objective"]) for row in rows]
    y = [float(row[y_column]) for row in rows]

    ax.scatter(x, y, s=14, alpha=0.52, edgecolors="none")

    fit = linear_fit(x, y)
    if fit is not None:
        slope, intercept = fit
        start = min(x)
        end = max(x)
        ax.plot(
            [start, end],
            [slope * start + intercept, slope * end + intercept],
            color="#c0392b",
            linewidth=1.6,
        )

    title = f"Podobieństwo do {title_scope}"
    if correlation is not None:
        title += f"\nKorelacja r = {correlation:.3f}"

    ax.set_title(title, fontsize=10)
    ax.set_xlabel("Wartość funkcji celu")
    ax.set_ylabel(y_label)
    ax.grid(True, linestyle="--", alpha=0.35)


def scatter(rows, y_column, y_label, title_scope, output_name, correlation):
    plt.figure(figsize=(9, 6))
    ax = plt.gca()
    draw_scatter(ax, rows, y_column, y_label, title_scope, correlation)
    plt.tight_layout()
    plt.savefig(os.path.join(PLOTS_DIR, output_name), dpi=300)
    plt.close()


def overview(rows, instance, correlations):
    fig, axes = plt.subplots(2, 2, figsize=(15, 10))
    axes = axes.flatten()

    for ax, (y_column, y_label, title_scope, _suffix, correlation_key) in zip(axes, PLOTS):
        correlation = correlations.get((instance, *correlation_key))
        draw_scatter(ax, rows, y_column, y_label, title_scope, correlation)

    fig.suptitle(
        f"{instance}: test globalnej wypukłości",
        fontsize=14,
    )
    fig.tight_layout(rect=[0, 0, 1, 0.96])
    fig.savefig(os.path.join(PLOTS_DIR, f"{instance}_overview.png"), dpi=300)
    plt.close(fig)


def main():
    if not os.path.exists(DETAILS_CSV):
        raise FileNotFoundError(f"Brak {DETAILS_CSV}. Najpierw uruchom java -cp out GlobalConvexityMain.")

    os.makedirs(PLOTS_DIR, exist_ok=True)
    correlations = load_correlations(CORRELATIONS_CSV)
    by_instance = defaultdict(list)
    for row in load_rows(DETAILS_CSV):
        by_instance[row["instance"]].append(row)

    for instance, rows in by_instance.items():
        overview(rows, instance, correlations)
        for y_column, y_label, title_scope, suffix, correlation_key in PLOTS:
            correlation = correlations.get((instance, *correlation_key))
            scatter(rows, y_column, y_label, title_scope, f"{instance}_{suffix}.png", correlation)

    print(f"Zapisano wykresy do {PLOTS_DIR}")


if __name__ == "__main__":
    main()
