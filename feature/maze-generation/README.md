# Maze Generation

A native maze generator with seven grid topologies and sixteen generation algorithms. It supports reproducible seeds, configurable algorithm parameters, dead-end removal, image masks, endpoint selection, shortest-path solving, distance heatmaps, and maze statistics.

The available grids are square, hexagonal, triangular, polar, octagonal-and-square, rhombic, and masked square. Algorithms include Recursive Backtracker, Randomized Prim, Randomized Kruskal, Aldous–Broder, Wilson, Hunt-and-Kill, Growing Tree, Eller, Sidewinder, Binary Tree, Recursive Division, Blobby Division, Houston, Reverse Delete, Growing Binary Tree, and Origin Shift.

Custom endpoints are selected with start-cell and end-cell index controls. The solution path and endpoint markers can be enabled independently. Their widths, sizes, colors, wall style, margins, and background transparency are configurable. Statistics include cell and passage counts, dead ends, junctions, solution length, and generation time.

SVG is the first output format. SVG previews retain vector content when zoomed; raster and SVG output share the same scene representation. Export filenames use the host application's filename settings, including prefix, suffix, timestamp, random, and checksum options.

Output dimensions follow the Noise Generation tool's per-side limit of 8192 pixels. Empty dimension fields are allowed while editing, and saving remains disabled until both dimensions are positive. Reverse Delete is limited to 1600 cells because it checks graph connectivity after each edge removal.

The screen restores its last parameters locally. Animation, named presets, and recipe import/export are not included.

## Tests

Unit tests cover generation, topology invariants, SVG rendering, and SVG filename creation. Android rendering tests cover bitmap output. Manual checks should include SVG zoom, transparent backgrounds, save/share for each format, and changing dimensions while editing.

## References

- Jamis Buck, [Maze Generation](https://weblog.jamisbuck.org/2011/2/7/maze-generation-algorithm-recap).
- Aldous, random-walk spanning-tree construction (1990).
- Wilson, loop-erased random walk (STOC 1996).
- [Origin Shift maze generation](https://github.com/CaptainLuma/New-Maze-Generating-Algorithm).

Growing Binary Tree uses the generalized preceding-BFS-layer parent variant. The Houston hybrid does not claim uniform spanning-tree sampling.
