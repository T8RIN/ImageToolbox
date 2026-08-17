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

package com.t8rin.imagetoolbox.core.settings.domain.model

data class MarkupLayerTextDefaults(
    val color: Int = DEFAULT_TEXT_COLOR,
    val size: Float = 0.5f,
    val font: String? = null,
    val backgroundColor: Int = 0,
    val decorations: List<MarkupLayerTextDecoration> = emptyList(),
    val outline: MarkupLayerOutline? = null,
    val alignment: MarkupLayerTextAlignment = MarkupLayerTextAlignment.Start,
    val geometricTransform: MarkupLayerTextGeometricTransform? = null,
    val shadow: MarkupLayerDropShadow? = null
) {
    companion object {
        val Default = MarkupLayerTextDefaults()
    }
}

private const val DEFAULT_TEXT_COLOR = -16777216

enum class MarkupLayerTextDecoration {
    Bold, Italic, Underline, LineThrough
}

enum class MarkupLayerTextAlignment {
    Start, Center, End
}

data class MarkupLayerOutline(
    val color: Int,
    val width: Float
)

data class MarkupLayerTextGeometricTransform(
    val scaleX: Float = 1f,
    val skewX: Float = 0f
)

data class MarkupLayerDropShadow(
    val color: Int = 0xFF000000.toInt(),
    val offsetX: Float = 0f,
    val offsetY: Float = 6f,
    val blurRadius: Float = 12f
) {
    companion object {
        val Default = MarkupLayerDropShadow()

        val BlurRadiusRange: ClosedFloatingPointRange<Float>
            get() = 0f..100f

        val OffsetXRange: ClosedFloatingPointRange<Float>
            get() = -64f..64f

        val OffsetYRange: ClosedFloatingPointRange<Float>
            get() = -64f..64f
    }
}
