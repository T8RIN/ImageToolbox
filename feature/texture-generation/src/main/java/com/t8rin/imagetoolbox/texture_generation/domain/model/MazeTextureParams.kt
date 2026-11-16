/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.texture_generation.domain.model

data class MazeTextureParams(
    val seed: Int = 1337,
    val columns: Int = 32,
    val rows: Int = 32,
    val algorithm: MazeAlgorithm = MazeAlgorithm.Backtracking,
    val wallWidth: Float = 0.12f,
    val padding: Float = 0.04f,
    val openings: Boolean = true,
    val directionBias: Float = 0.5f,
    val branching: Float = 0.5f,
    val braid: Float = 0f
)

enum class MazeAlgorithm {
    Backtracking,
    Prim,
    Kruskal,
    AldousBroder,
    Wilson,
    HuntAndKill,
    GrowingTree,
    Eller,
    Sidewinder,
    BinaryTree,
    RecursiveDivision,
    BlobbyDivision,
    Houston,
    ReverseDelete,
    GrowingBinaryTree,
    OriginShift
}
