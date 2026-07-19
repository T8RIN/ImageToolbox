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

package com.t8rin.imagetoolbox.feature.image_stitch.domain

sealed class StitchMode(val ordinal: Int) {
    open val topDrop: Int = 0
    open val bottomDrop: Int = 0
    open val startDrop: Int = 0
    open val endDrop: Int = 0

    fun drops(): List<Int> = if (isAuto()) {
        listOf(topDrop, bottomDrop, startDrop, endDrop)
    } else emptyList()

    fun isAuto(): Boolean = this is Auto || this is Panorama

    fun copyWithDrops(
        topDrop: Int = this.topDrop,
        bottomDrop: Int = this.bottomDrop,
        startDrop: Int = this.startDrop,
        endDrop: Int = this.endDrop
    ): StitchMode = when (this) {
        is Auto -> copy(
            topDrop = topDrop,
            bottomDrop = bottomDrop,
            startDrop = startDrop,
            endDrop = endDrop
        )

        is Panorama -> copy(
            topDrop = topDrop,
            bottomDrop = bottomDrop,
            startDrop = startDrop,
            endDrop = endDrop
        )

        else -> this
    }

    data class Auto(
        override val topDrop: Int = 0,
        override val bottomDrop: Int = 0,
        override val startDrop: Int = 0,
        override val endDrop: Int = 0
    ) : StitchMode(-1) {
        constructor(drops: List<Int>) : this(
            topDrop = drops.getOrNull(0) ?: 0,
            bottomDrop = drops.getOrNull(1) ?: 0,
            startDrop = drops.getOrNull(2) ?: 0,
            endDrop = drops.getOrNull(3) ?: 0
        )
    }

    data class Panorama(
        override val topDrop: Int = 0,
        override val bottomDrop: Int = 0,
        override val startDrop: Int = 0,
        override val endDrop: Int = 0
    ) : StitchMode(4) {
        constructor(drops: List<Int>) : this(
            topDrop = drops.getOrNull(0) ?: 0,
            bottomDrop = drops.getOrNull(1) ?: 0,
            startDrop = drops.getOrNull(2) ?: 0,
            endDrop = drops.getOrNull(3) ?: 0
        )
    }

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
            -1 -> Auto()
            0 -> Horizontal
            1 -> Vertical
            2 -> Grid.Horizontal()
            3 -> Grid.Vertical()
            4 -> Panorama()
            else -> Horizontal
        }

        val entries by lazy {
            listOf(
                Horizontal,
                Vertical,
                Grid.Horizontal(),
                Grid.Vertical(),
                Auto(),
                Panorama()
            )
        }
    }
}