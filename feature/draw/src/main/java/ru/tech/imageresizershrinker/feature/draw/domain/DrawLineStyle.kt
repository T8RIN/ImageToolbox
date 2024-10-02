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

package ru.tech.imageresizershrinker.feature.draw.domain

import ru.tech.imageresizershrinker.core.domain.model.Pt
import ru.tech.imageresizershrinker.core.domain.model.pt

// Works only for Basic draw modes (excludes DrawMode.PathEffect, DrawMode.SpotHeal, DrawMode.Text, DrawMode.Image)
sealed class DrawLineStyle(
    val ordinal: Int
) {
    data object None : DrawLineStyle(0)

    data class Dashed(
        val size: Pt = 10.pt,
        val gap: Pt = 20.pt
    ) : DrawLineStyle(1)

    data class ZigZag(
        val heightRatio: Float = 4f
    ) : DrawLineStyle(2)

    data class Stamped<Shape>(
        val shape: Shape? = null,
        val spacing: Pt = 20.pt
    ) : DrawLineStyle(3)

    data object DotDashed : DrawLineStyle(4)

    companion object {
        val entries by lazy {
            listOf(
                None,
                Dashed(),
                DotDashed,
                ZigZag(),
                Stamped<Any>(),
            )
        }

        fun fromOrdinal(
            ordinal: Int
        ): DrawLineStyle = entries.find {
            it.ordinal == ordinal
        } ?: None
    }
}