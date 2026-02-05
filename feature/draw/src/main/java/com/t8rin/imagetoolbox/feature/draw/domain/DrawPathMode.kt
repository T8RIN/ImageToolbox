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

package com.t8rin.imagetoolbox.feature.draw.domain

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Pt
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.domain.utils.safeCast
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.FloodFill
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.Lasso
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.OutlinedOval
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.OutlinedRect
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.Oval
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.Polygon
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.Rect
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.Spray
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.Star
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode.Triangle

sealed class DrawPathMode(
    val ordinal: Int
) {
    sealed class Outlined(ordinal: Int) : DrawPathMode(ordinal) {
        abstract val fillColor: ColorModel?
    }

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
        val rotationDegrees: Int = 0,
        val cornerRadius: Float = 0f,
        override val fillColor: ColorModel? = null
    ) : Outlined(7)

    data class OutlinedOval(
        override val fillColor: ColorModel? = null
    ) : Outlined(8)

    data class Rect(
        val rotationDegrees: Int = 0,
        val cornerRadius: Float = 0f
    ) : DrawPathMode(9)

    data object Oval : DrawPathMode(10)
    data object Triangle : DrawPathMode(11)
    data class OutlinedTriangle(
        override val fillColor: ColorModel? = null
    ) : Outlined(12)

    data class Polygon(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val isRegular: Boolean = false
    ) : DrawPathMode(13)

    data class OutlinedPolygon(
        val vertices: Int = 5,
        val rotationDegrees: Int = 0,
        val isRegular: Boolean = false,
        override val fillColor: ColorModel? = null
    ) : Outlined(14)

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
        val isRegular: Boolean = false,
        override val fillColor: ColorModel? = null
    ) : Outlined(16)

    data class FloodFill(
        val tolerance: Float = 0.25f
    ) : DrawPathMode(17) {
        companion object {
            val StrokeSize = 2f.pt
        }
    }

    data class Spray(
        val density: Int = 50,
        val pixelSize: Float = 1f,
        val isSquareShaped: Boolean = false
    ) : DrawPathMode(18)

    val canChangeStrokeWidth: Boolean
        get() = this !is FloodFill && (!isFilled || this is Spray)

    val isFilled: Boolean
        get() = filled.any { this::class.isInstance(it) }

    val outlinedFillColor: ColorModel?
        get() = this.safeCast<Outlined>()?.fillColor

    val isSharpEdge: Boolean
        get() = sharp.any { this::class.isInstance(it) }

    companion object {
        val entries by lazy {
            listOf(
                Free,
                FloodFill(),
                Spray(),
                Line,
                PointingArrow(),
                DoublePointingArrow(),
                LinePointingArrow(),
                DoubleLinePointingArrow(),
                Lasso,
                OutlinedRect(),
                OutlinedOval(),
                OutlinedTriangle(),
                OutlinedPolygon(),
                OutlinedStar(),
                Rect(),
                Oval,
                Triangle,
                Polygon(),
                Star()
            )
        }

        val outlinedEntries by lazy {
            listOf(
                OutlinedRect(),
                OutlinedOval(),
                OutlinedTriangle(),
                OutlinedPolygon(),
                OutlinedStar()
            )
        }

        fun fromOrdinal(
            ordinal: Int
        ): DrawPathMode = entries.find {
            it.ordinal == ordinal
        } ?: Free
    }

    fun convertStrokeWidth(
        strokeWidth: Pt,
        canvasSize: IntegerSize
    ): Float = when (this) {
        is FloodFill -> FloodFill.StrokeSize.toPx(canvasSize)
        else -> strokeWidth.toPx(canvasSize)
    }
}

private val filled = listOf(
    Lasso,
    Rect(),
    Oval,
    Triangle,
    Polygon(),
    Star(),
    Spray()
)

private val sharp = listOf(
    OutlinedRect(),
    OutlinedOval(),
    Rect(),
    Oval,
    Lasso,
    FloodFill(),
    Spray()
)