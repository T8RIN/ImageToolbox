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

enum class CellularGridType(
    val value: Int
) {
    Random(0),
    Square(1),
    Hexagonal(2),
    Octagonal(3),
    Triangular(4)
}

enum class FbmBasisType(
    val value: Int
) {
    Noise(0),
    Ridged(1),
    VlNoise(2),
    ScNoise(3),
    Cellular(4)
}
