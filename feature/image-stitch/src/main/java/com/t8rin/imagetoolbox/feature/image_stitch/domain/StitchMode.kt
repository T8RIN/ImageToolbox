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

package com.t8rin.imagetoolbox.feature.image_stitch.domain

sealed class StitchMode(val ordinal: Int) {
    data object Horizontal : StitchMode(0)

    data object Vertical : StitchMode(1)

    sealed class Grid(ordinal: Int) : StitchMode(ordinal) {
        data class Horizontal(val rows: Int = 2) : Grid(2)
        data class Vertical(val columns: Int = 2) : Grid(3)
    }

    fun gridCellsCount(): Int {
        return when (this) {
            is Grid.Horizontal -> this.rows
            is Grid.Vertical -> this.columns
            else -> 0
        }
    }

    fun isHorizontal(): Boolean = this is Horizontal || this is Grid.Horizontal

    companion object {
        fun fromOrdinal(ordinal: Int) = when (ordinal) {
            0 -> Horizontal
            1 -> Vertical
            2 -> Grid.Horizontal()
            3 -> Grid.Vertical()
            else -> Horizontal
        }
    }
}