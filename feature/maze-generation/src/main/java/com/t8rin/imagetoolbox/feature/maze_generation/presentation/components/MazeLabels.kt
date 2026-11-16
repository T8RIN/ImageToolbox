/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.maze_generation.presentation.components

import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.maze_generation.domain.*

internal val MazeTopology.title: Int
    get() =
        when (this) {
            MazeTopology.Square -> R.string.maze_topology_square
            MazeTopology.Hexagonal -> R.string.maze_topology_hexagonal
            MazeTopology.Triangular -> R.string.maze_topology_triangular
            MazeTopology.Polar -> R.string.maze_topology_polar
            MazeTopology.Upsilon -> R.string.maze_topology_upsilon
            MazeTopology.Rhombic -> R.string.maze_topology_rhombic
            MazeTopology.Mask -> R.string.maze_topology_mask
        }

internal val MazeAlgorithm.title: Int
    get() =
        when (this) {
            MazeAlgorithm.Backtracker -> R.string.maze_algorithm_backtracker
            MazeAlgorithm.Prim -> R.string.maze_algorithm_prim
            MazeAlgorithm.Kruskal -> R.string.maze_algorithm_kruskal
            MazeAlgorithm.AldousBroder -> R.string.maze_algorithm_aldous_broder
            MazeAlgorithm.Wilson -> R.string.maze_algorithm_wilson
            MazeAlgorithm.HuntAndKill -> R.string.maze_algorithm_hunt_and_kill
            MazeAlgorithm.GrowingTree -> R.string.maze_algorithm_growing_tree
            MazeAlgorithm.Eller -> R.string.maze_algorithm_eller
            MazeAlgorithm.Sidewinder -> R.string.maze_algorithm_sidewinder
            MazeAlgorithm.BinaryTree -> R.string.maze_algorithm_binary_tree
            MazeAlgorithm.RecursiveDivision -> R.string.maze_algorithm_recursive_division
            MazeAlgorithm.BlobbyDivision -> R.string.maze_algorithm_blobby_division
            MazeAlgorithm.Houston -> R.string.maze_algorithm_houston
            MazeAlgorithm.ReverseDelete -> R.string.maze_algorithm_reverse_delete
            MazeAlgorithm.GrowingBinaryTree -> R.string.maze_algorithm_growing_binary_tree
            MazeAlgorithm.OriginShift -> R.string.maze_algorithm_origin_shift
        }

internal val MazeMask.title: Int
    get() =
        when (this) {
            MazeMask.Ellipse -> R.string.maze_mask_ellipse
            MazeMask.Diamond -> R.string.maze_mask_diamond
            MazeMask.Heart -> R.string.maze_mask_heart
            MazeMask.Cross -> R.string.maze_mask_cross
            MazeMask.Image -> R.string.maze_mask_image
        }

internal val MazeEntrance.title: Int
    get() =
        when (this) {
            MazeEntrance.Farthest -> R.string.maze_entrance_farthest
            MazeEntrance.Opposite -> R.string.maze_entrance_opposite
            MazeEntrance.Random -> R.string.maze_entrance_random
            MazeEntrance.Center -> R.string.maze_entrance_center
            MazeEntrance.Manual -> R.string.maze_entrance_manual
        }

internal val MazeDirection.title: Int
    get() =
        when (this) {
            MazeDirection.NorthEast -> R.string.maze_direction_north_east
            MazeDirection.NorthWest -> R.string.maze_direction_north_west
            MazeDirection.SouthEast -> R.string.maze_direction_south_east
            MazeDirection.SouthWest -> R.string.maze_direction_south_west
        }

internal val MazeCap.title: Int
    get() =
        when (this) {
            MazeCap.Butt -> R.string.maze_cap_butt
            MazeCap.Round -> R.string.maze_cap_round
            MazeCap.Square -> R.string.maze_cap_square
        }

internal val MazeAlgorithm.description: Int
    get() =
        when (this) {
            MazeAlgorithm.Backtracker -> R.string.maze_hint_backtracker
            MazeAlgorithm.Prim -> R.string.maze_hint_prim
            MazeAlgorithm.Kruskal -> R.string.maze_hint_kruskal
            MazeAlgorithm.AldousBroder,
            MazeAlgorithm.Wilson -> R.string.maze_uniform_hint
            MazeAlgorithm.HuntAndKill -> R.string.maze_hint_hunt
            MazeAlgorithm.GrowingTree -> R.string.maze_hint_growing
            MazeAlgorithm.Eller -> R.string.maze_hint_eller
            MazeAlgorithm.Sidewinder -> R.string.maze_hint_sidewinder
            MazeAlgorithm.BinaryTree -> R.string.maze_hint_binary
            MazeAlgorithm.RecursiveDivision -> R.string.maze_hint_division
            MazeAlgorithm.BlobbyDivision -> R.string.maze_hint_blobby
            MazeAlgorithm.Houston -> R.string.maze_houston_hint
            MazeAlgorithm.ReverseDelete -> R.string.maze_reverse_hint
            MazeAlgorithm.GrowingBinaryTree -> R.string.maze_hint_growing_binary
            MazeAlgorithm.OriginShift -> R.string.maze_origin_hint
        }
