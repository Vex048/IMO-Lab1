import os
import csv
import argparse
from pathlib import Path

# Try importing pandas, fallback to standard csv if not available
try:
    import pandas as pd
    HAS_PANDAS = True
except ImportError:
    HAS_PANDAS = False

import matplotlib.pyplot as plt
from matplotlib.collections import LineCollection

def load_dataset(path: str):
    nodes = []
    with open(path, newline="") as f:
        reader = csv.reader(f, delimiter=";")
        for row in reader:
            if not row or all(cell.strip() == "" for cell in row):
                continue
            try:
                x = int(row[0])
                y = int(row[1])
                reward = int(row[2])
            except Exception:
                continue
            nodes.append((x, y, reward))
    return nodes

def load_tour_indices(path: str):
    inds = []
    with open(path, "r") as f:
        for line in f:
            line = line.strip()
            if line == "":
                continue
            inds.append(int(line))
    return inds

def plot_static_tour(dataset_path, tour_path, title, output_path):
    nodes = load_dataset(dataset_path)
    tour = load_tour_indices(tour_path)
    
    # Configure node sizes based on rewards
    rewards = [n[2] for n in nodes]
    if rewards:
        min_r, max_r = min(rewards), max(rewards)
        if max_r == min_r:
            node_sizes = [50.0] * len(rewards)
        else:
            node_sizes = [20.0 + (r - min_r) / (max_r - min_r) * 60.0 for r in rewards]
    else:
        node_sizes = [30.0] * len(nodes)
        
    # Generate segments for the tour
    segs = []
    for i in range(len(tour)):
        idx_from = tour[i]
        idx_to = tour[(i + 1) % len(tour)]
        x1, y1 = nodes[idx_from][0], nodes[idx_from][1]
        x2, y2 = nodes[idx_to][0], nodes[idx_to][1]
        segs.append(((x1, y1), (x2, y2)))
        
    all_xs = [n[0] for n in nodes]
    all_ys = [n[1] for n in nodes]
    
    fig, ax = plt.subplots(figsize=(10, 8))
    
    # Plot all nodes
    ax.scatter(all_xs, all_ys, color="gray", s=node_sizes, alpha=0.6, zorder=1)
    
    # Add labels
    for idx, (x, y, reward) in enumerate(nodes):
        ax.text(x + 1, y + 1, f"{idx+1}", fontsize=8)
        
    # Plot the lines
    lc = LineCollection(segs, colors="tab:blue", linewidths=1.5, zorder=2)
    ax.add_collection(lc)
    
    ax.set_aspect("equal", adjustable="datalim")
    ax.grid(True, linestyle="--", alpha=0.4)
    ax.set_title(title, fontsize=14)
    
    plt.tight_layout()
    plt.savefig(output_path, dpi=300)
    plt.close()
    print(f"Saved plot to: {output_path}")

def main():
    best_worst_csv = 'outputs/best_worst_runs.csv'
    output_dir = 'outputs/plots'
    dataset_tspa = 'datasets/TSPA.csv'
    
    os.makedirs(output_dir, exist_ok=True)
    
    if not os.path.exists(best_worst_csv):
        print(f"Error: {best_worst_csv} not found.")
        return
        
    print("Generating static plots for absolute best TSPA solutions...")
    
    # Using standard CSV reader to avoid pandas dependency issues just in case
    with open(best_worst_csv, newline='') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            if row['instance'] == 'TSPA':
                method = row['method']
                best_tour_file = row['bestTourFile']
                objective = row['bestObjective']
                
                # Check if file exists, occasionally paths in CSV might need tweaking if run from different dir
                # The CSV might store 'outputs/runs/...' so from root it should be accessible
                # If we are inside scripts/python, we need to adjust to project root
                
                # Assume script is run from project root (like python scripts/python/plot_best_solutions.py)
                tour_path = best_tour_file
                if not os.path.exists(tour_path):
                    # Try going up directories
                    tour_path = os.path.join("../../", best_tour_file)
                    dataset_tspa = "../../datasets/TSPA.csv"
                    output_dir = "../../outputs/plots"
                    os.makedirs(output_dir, exist_ok=True)
                
                if not os.path.exists(tour_path):
                    print(f"Tour file not found: {tour_path}")
                    continue
                    
                title = f"{method} (TSPA)\nBest Objective: {objective}"
                clean_method_name = method.replace(" ", "_").replace("/", "_")
                output_path = os.path.join(output_dir, f"TSPA_best_{clean_method_name}.png")
                
                plot_static_tour(dataset_tspa, tour_path, title, output_path)
                
    print("Done!")

if __name__ == '__main__':
    main()
