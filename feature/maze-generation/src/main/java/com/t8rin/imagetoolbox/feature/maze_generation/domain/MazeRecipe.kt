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

package com.t8rin.imagetoolbox.feature.maze_generation.domain

import kotlinx.serialization.Serializable

@Serializable
enum class MazeTopology {
    Square,
    Hexagonal,
    Triangular,
    Polar,
    Upsilon,
    Rhombic,
    Mask,
}

@Serializable
enum class MazeAlgorithm(val squareOnly: Boolean = false, val expensive: Boolean = false) {
    Backtracker,
    Prim,
    Kruskal,
    AldousBroder(expensive = true),
    Wilson(expensive = true),
    HuntAndKill,
    GrowingTree,
    Eller(true),
    Sidewinder(true),
    BinaryTree(true),
    RecursiveDivision(true),
    BlobbyDivision,
    Houston(expensive = true),
    ReverseDelete(expensive = true),
    GrowingBinaryTree,
    OriginShift;

    fun supports(topology: MazeTopology) = !squareOnly || topology == MazeTopology.Square
}

@Serializable
enum class MazeMask {
    Ellipse,
    Diamond,
    Heart,
    Cross,
    Image,
}

@Serializable
enum class MazeEntrance {
    Farthest,
    Opposite,
    Random,
    Center,
    Manual,
}

@Serializable
enum class MazeDirection {
    NorthEast,
    NorthWest,
    SouthEast,
    SouthWest,
}

@Serializable
enum class MazeCap {
    Butt,
    Round,
    Square,
}

/** Versioned, self-contained input. Image masks contain sampled cells, never external URIs. */
@Serializable
data class MazeRecipe(
    val version: Int = 1,
    val algorithmVersion: Int = 1,
    val topology: MazeTopology = MazeTopology.Square,
    val algorithm: MazeAlgorithm = MazeAlgorithm.Backtracker,
    val rows: Int = 24,
    val columns: Int = 24,
    val seed: Int = 42,
    val braid: Int = 0,
    val newestWeight: Int = 75,
    val randomWeight: Int = 25,
    val oldestWeight: Int = 0,
    val middleWeight: Int = 0,
    val direction: MazeDirection = MazeDirection.NorthEast,
    val horizontalBias: Int = 50,
    val ellerJoin: Int = 50,
    val ellerDrop: Int = 50,
    val houstonSwitch: Int = 30,
    val originIterations: Int = 10,
    val divisionRoom: Int = 1,
    val mask: MazeMask = MazeMask.Ellipse,
    val imageMask: List<Boolean> = emptyList(),
    val entrance: MazeEntrance = MazeEntrance.Farthest,
    val startCell: Int = 0,
    val endCell: Int = 1,
    val openEntrances: Boolean = true,
    val style: MazeStyle = MazeStyle(),
) {
    fun validated(): MazeRecipe {
        require(version == 1 && algorithmVersion == 1) { "Unsupported maze recipe version" }
        require(algorithm.supports(topology)) { "Algorithm requires a square grid" }
        require(rows in 2..100 && columns in 2..100)
        require(startCell >= 0 && endCell >= 0)
        require(
            listOf(
                    braid,
                    newestWeight,
                    randomWeight,
                    oldestWeight,
                    middleWeight,
                    horizontalBias,
                    ellerJoin,
                    ellerDrop,
                    houstonSwitch,
                )
                .all { it in 0..100 }
        )
        require(originIterations in 1..100 && divisionRoom in 1..20)
        require(imageMask.size <= 10000)
        if (topology == MazeTopology.Mask && mask == MazeMask.Image) {
            require(imageMask.size == rows * columns && imageMask.any { it }) { "Empty image mask" }
        }
        style.validated()
        return this
    }

    fun graphKey() =
        copy(
            style = MazeStyle(),
            entrance = MazeEntrance.Farthest,
            startCell = 0,
            endCell = 1,
            openEntrances = true,
        )
}

@Serializable
data class MazeStyle(
    val wallColor: Int = 0xff161616.toInt(),
    val backgroundColor: Int = 0xffffffff.toInt(),
    val solutionColor: Int = 0xffe53935.toInt(),
    val startColor: Int = 0xff43a047.toInt(),
    val endColor: Int = 0xff1e88e5.toInt(),
    val wallWidth: Float = 0.08f,
    val solutionWidth: Float = 0.18f,
    val markerSize: Float = 0.25f,
    val margin: Float = 0.6f,
    val cap: MazeCap = MazeCap.Round,
    val transparent: Boolean = false,
    val showSolution: Boolean = false,
    val showMarkers: Boolean = true,
    val heatmap: Boolean = false,
) {
    fun validated() {
        require(wallWidth.isFinite() && wallWidth in 0.01f..0.5f)
        require(solutionWidth.isFinite() && solutionWidth in 0.02f..0.8f)
        require(markerSize.isFinite() && markerSize in 0.05f..0.6f)
        require(margin.isFinite() && margin in 0.1f..5f)
    }
}
