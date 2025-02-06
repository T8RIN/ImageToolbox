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

sealed class DrawPathMode(
    val ordinal: Int
) {

    data object Free : DrawPathMode(0)
    data object Line : DrawPathMode(1)

    data class PointingArrow(
        val sizeScale: Float = 3f,
        val angle: Float = 150f
    ) : DrawPathMode(2)

    data class DoublePointingArrow(
        val sizeScale: Float = 3f,
        val angle: Float = 150f
    ) : DrawPathMode(3)

    data class LinePointingArrow(
        val sizeScale: Float = 3f,
        val angle: Float = 150f
    ) : DrawPathMode(4)

    data class DoubleLinePointingArrow(
        val sizeScale: Float = 3f,
        val angle: Float = 150f
    ) : DrawPathMode(5)

    data object Lasso : DrawPathMode(6)

    data class OutlinedRect(
        val rotationDegrees: Int = 0
    ) : DrawPathMode(7)

    data object OutlinedOval : DrawPathMode(8)

    data class Rect(
        val rotationDegrees: Int = 0
    ) : DrawPathMode(9)

    data object Oval : DrawPathMode(10)
    data object Triangle : DrawPathMode(11)
    data object OutlinedTriangle : DrawPathMode(12)

    data class Polygon(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val isRegular: Boolean = false
    ) : DrawPathMode(13)

    data class OutlinedPolygon(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val isRegular: Boolean = false
    ) : DrawPathMode(14)

    data class Star(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val innerRadiusRatio: Float = 0.5f,
        val isRegular: Boolean = false
    ) : DrawPathMode(15)

    data class OutlinedStar(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val innerRadiusRatio: Float = 0.5f,
        val isRegular: Boolean = false
    ) : DrawPathMode(16)

    val isStroke: Boolean
        get() = !isFilled

    val isFilled: Boolean
        get() = listOf(Lasso, Rect(), Oval, Triangle, Polygon(), Star()).any {
            this::class.isInstance(it)
        }

    val isSharpEdge: Boolean
        get() = listOf(OutlinedRect(), OutlinedOval, Rect(), Oval, Lasso).any {
            this::class.isInstance(it)
        }

    companion object {
        val entries by lazy {
            listOf(
                Free,
                Line,
                PointingArrow(),
                DoublePointingArrow(),
                LinePointingArrow(),
                DoubleLinePointingArrow(),
                Lasso,
                OutlinedRect(),
                OutlinedOval,
                OutlinedTriangle,
                OutlinedPolygon(),
                OutlinedStar(),
                Rect(),
                Oval,
                Triangle,
                Polygon(),
                Star()
            )
        }

        fun fromOrdinal(
            ordinal: Int
        ): DrawPathMode = entries.find {
            it.ordinal == ordinal
        } ?: Free
    }
}