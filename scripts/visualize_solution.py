from pathlib import Path
import csv

import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation
from matplotlib.collections import LineCollection
from matplotlib.lines import Line2D

try:
    from matplotlib.animation import PillowWriter, FFMpegWriter
except Exception:
    PillowWriter = None
    FFMpegWriter = None


# include text files with solution for visualization
DEFAULT_RUNS = [
    {
        "tourfile": "outputs/runs/greedy_cycle_distance/TSPA/run_029.txt",
        "label": "Greedy Cycle (distance)",
        "color": "tab:orange",
    },
    {
        "tourfile": "outputs/runs/greedy_cycle_objective/TSPA/run_025.txt",
        "label": "Greedy Cycle (cost)",
        "color": "tab:blue",
    },
]

DEFAULT_COMBINED_OUTPUT = "outputs/greedyCycle-distance-cost.mp4"
DEFAULT_COLORS = [
    "tab:blue",
    "tab:orange",
    "tab:green",
    "tab:red",
    "tab:purple",
    "tab:brown",
    "tab:pink",
    "tab:gray",
]

DEFAULT_DATASET = "datasets/TSPA.csv"
DEFAULT_SHOW = False
DEFAULT_INTERVAL_MS = 300
DEFAULT_FPS = None
DEFAULT_SAVE_INDIVIDUAL = False
DEFAULT_SAVE_DPI = 100


DEFAULT_NODE_SIZE_MIN = 20
DEFAULT_NODE_SIZE_MAX = 80
DEFAULT_PAD_FRAC = 0.05
DEFAULT_SPACE_SCALE = 1.5
DEFAULT_PAD_MIN = 1.0
DEFAULT_LABEL_OFFSET_FRAC = 0.01


def load_dataset(path: str) -> list[tuple[int, int, int]]:
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


def load_tour_indices(path: str) -> list[int]:
    inds = []
    with open(path, "r") as f:
        for line in f:
            line = line.strip()
            if line == "":
                continue
            inds.append(int(line))
    return inds


def visualize_multi(
    dataset_path,
    runs,
    show=True,
    combined_save_path=None,
    interval_ms=500,
    fps=None,
    save_individual=False,
    save_dpi=DEFAULT_SAVE_DPI,
):
    nodes = load_dataset(dataset_path)

    rewards = [n[2] for n in nodes]
    if rewards:
        min_r = min(rewards)
        max_r = max(rewards)
        if max_r == min_r:
            node_sizes = [(DEFAULT_NODE_SIZE_MIN + DEFAULT_NODE_SIZE_MAX) / 2.0] * len(
                rewards
            )
        else:
            node_sizes = [
                DEFAULT_NODE_SIZE_MIN
                + (r - min_r)
                / (max_r - min_r)
                * (DEFAULT_NODE_SIZE_MAX - DEFAULT_NODE_SIZE_MIN)
                for r in rewards
            ]
    else:
        node_sizes = [DEFAULT_NODE_SIZE_MIN] * len(nodes)

    norm_runs = []
    for idx, r in enumerate(runs):
        if isinstance(r, (str, Path)):
            tourfile = Path(r)
            label = tourfile.stem
            color = DEFAULT_COLORS[idx % len(DEFAULT_COLORS)]
            norm_runs.append(
                {"tourfile": str(tourfile), "label": label, "color": color}
            )
        else:
            tourfile = Path(r.get("tourfile"))
            label = r.get("label") or tourfile.stem
            color = r.get("color") or DEFAULT_COLORS[idx % len(DEFAULT_COLORS)]
            norm_runs.append(
                {
                    "tourfile": str(tourfile),
                    "label": label,
                    "color": color,
                    "output": r.get("output"),
                }
            )

    segments_per_run = []
    labels = []
    colors = []
    for r in norm_runs:
        tour = load_tour_indices(r["tourfile"])
        segs = []
        for i in range(len(tour)):
            idx_from = tour[i]
            idx_to = tour[(i + 1) % len(tour)]
            x1, y1 = nodes[idx_from][0], nodes[idx_from][1]
            x2, y2 = nodes[idx_to][0], nodes[idx_to][1]
            segs.append(((x1, y1), (x2, y2)))
        segments_per_run.append(segs)
        labels.append(r.get("label"))
        colors.append(r.get("color"))

    max_len = max(len(s) for s in segments_per_run)

    fig, ax = plt.subplots(figsize=(8, 6))
    all_xs = [n[0] for n in nodes]
    all_ys = [n[1] for n in nodes]

    minx, maxx = min(all_xs), max(all_xs)
    miny, maxy = min(all_ys), max(all_ys)
    range_x = maxx - minx
    range_y = maxy - miny
    padx = (
        range_x * DEFAULT_PAD_FRAC * DEFAULT_SPACE_SCALE
        if range_x != 0
        else DEFAULT_PAD_MIN * DEFAULT_SPACE_SCALE
    )
    pady = (
        range_y * DEFAULT_PAD_FRAC * DEFAULT_SPACE_SCALE
        if range_y != 0
        else DEFAULT_PAD_MIN * DEFAULT_SPACE_SCALE
    )

    label_offset = (
        max(range_x, range_y) * DEFAULT_LABEL_OFFSET_FRAC * DEFAULT_SPACE_SCALE
    )
    if label_offset == 0:
        label_offset = DEFAULT_PAD_MIN * 0.5

    ax.scatter(all_xs, all_ys, color="gray", s=node_sizes, alpha=0.6, zorder=1)
    for idx, (x, y, reward) in enumerate(nodes):
        ax.text(x + label_offset, y + label_offset, f"{idx+1}", fontsize=8)

    lcs = []
    for i, segs in enumerate(segments_per_run):
        lc = LineCollection([], colors=colors[i], linewidths=1.5, zorder=2 + i)
        ax.add_collection(lc)
        lcs.append(lc)

    ax.set_aspect("equal", adjustable="datalim")
    ax.grid(True, linestyle="--", alpha=0.4)
    ax.set_xlim(minx - padx, maxx + padx)
    ax.set_ylim(miny - pady, maxy + pady)

    handles = [Line2D([0], [0], color=c, lw=2) for c in colors]
    ax.legend(handles, labels)

    title = ax.set_title(f"Tour progress: 0/{max_len} — Autoplay (multi)")

    def update(frame):
        for i, segs in enumerate(segments_per_run):
            cur = min(frame + 1, len(segs))
            lcs[i].set_segments(segs[:cur])
        title.set_text(
            f"Tour progress: {min(frame+1, max_len)}/{max_len} — Autoplay (multi)"
        )
        return lcs

    anim = FuncAnimation(
        fig, update, frames=max_len, interval=interval_ms, blit=False, repeat=True
    )

    if combined_save_path:
        out = Path(combined_save_path)
        ext = out.suffix.lower()
        writer = None
        if ext == ".gif":
            if PillowWriter is None:
                raise SystemExit(
                    "PillowWriter not available; install pillow to save GIFs"
                )
            use_fps = (
                fps if fps is not None else max(1, int(round(1000.0 / interval_ms)))
            )
            writer = PillowWriter(fps=use_fps)
        elif ext in (".mp4", ".m4v"):
            if FFMpegWriter is None:
                raise SystemExit(
                    "FFMpegWriter not available; install ffmpeg to save MP4s"
                )
            use_fps = (
                fps if fps is not None else max(1, int(round(1000.0 / interval_ms)))
            )
            writer = FFMpegWriter(fps=use_fps)
        else:
            if PillowWriter is not None:
                use_fps = (
                    fps if fps is not None else max(1, int(round(1000.0 / interval_ms)))
                )
                writer = PillowWriter(fps=use_fps)
                out = out.with_suffix(".gif")
            else:
                raise SystemExit(
                    "Unknown output format and Pillow not available to write GIFs"
                )

        try:
            anim.save(str(out), writer=writer, dpi=save_dpi)
            print(f"Saved combined animation to {out}")
        except Exception as e:
            print("Failed to save combined animation:", e)

    if save_individual:
        for idx, r in enumerate(norm_runs):
            run_out = r.get("output")
            if not run_out:
                continue
            segs = segments_per_run[idx]
            if not segs:
                continue
            fig2, ax2 = plt.subplots(figsize=(8, 6))
            ax2.scatter(all_xs, all_ys, color="gray", s=node_sizes, alpha=0.6, zorder=1)
            for idn, (x, y, reward) in enumerate(nodes):
                ax2.text(x + 0.5, y + 0.5, f"{idn+1}", fontsize=8)
            lc2 = LineCollection([], colors=colors[idx], linewidths=1.5, zorder=2)
            ax2.add_collection(lc2)
            ax2.set_aspect("equal", adjustable="datalim")
            ax2.grid(True, linestyle="--", alpha=0.4)
            ax2.set_xlim(minx - padx, maxx + padx)
            ax2.set_ylim(miny - pady, maxy + pady)

            def upd2(frame2):
                lc2.set_segments(segs[: frame2 + 1])
                return [lc2]

            anim2 = FuncAnimation(
                fig2,
                upd2,
                frames=len(segs),
                interval=interval_ms,
                blit=False,
                repeat=False,
            )
            out2 = Path(run_out)
            ext2 = out2.suffix.lower()
            writer2 = None
            if ext2 == ".gif":
                if PillowWriter is None:
                    print("Skipping per-run save (Pillow not available)")
                    continue
                writer2 = PillowWriter(
                    fps=(
                        fps
                        if fps is not None
                        else max(1, int(round(1000.0 / interval_ms)))
                    )
                )
            elif ext2 in (".mp4", ".m4v"):
                if FFMpegWriter is None:
                    print("Skipping per-run save (ffmpeg not available)")
                    continue
                writer2 = FFMpegWriter(
                    fps=(
                        fps
                        if fps is not None
                        else max(1, int(round(1000.0 / interval_ms)))
                    )
                )
            else:
                if PillowWriter is not None:
                    writer2 = PillowWriter(
                        fps=(
                            fps
                            if fps is not None
                            else max(1, int(round(1000.0 / interval_ms)))
                        )
                    )
                    out2 = out2.with_suffix(".gif")
                else:
                    print("Skipping per-run save (unknown format)")
                    continue
            try:
                anim2.save(str(out2), writer=writer2, dpi=save_dpi)
                print(f"Saved per-run animation to {out2}")
            except Exception as e:
                print("Failed to save per-run animation for", r.get("label"), e)

    if show:
        plt.show()
    else:
        plt.close(fig)


if __name__ == "__main__":
    visualize_multi(
        Path(DEFAULT_DATASET),
        DEFAULT_RUNS,
        show=DEFAULT_SHOW,
        combined_save_path=DEFAULT_COMBINED_OUTPUT,
        interval_ms=DEFAULT_INTERVAL_MS,
        fps=DEFAULT_FPS,
        save_individual=DEFAULT_SAVE_INDIVIDUAL,
        save_dpi=DEFAULT_SAVE_DPI,
    )
