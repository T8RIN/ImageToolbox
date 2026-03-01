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

package com.t8rin.imagetoolbox.feature.markup_layers.domain

import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Outline
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType

data class MarkupLayer(
    val type: LayerType,
    val position: LayerPosition
)

data class LayerPosition(
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val alpha: Float = 1f,
    val currentCanvasSize: IntegerSize,
    val coerceToBounds: Boolean
) {
    companion object {
        fun LayerPosition.adjustByCanvasSize(
            value: IntegerSize
        ): LayerPosition {
            var scale = this.scale
            var offsetX = this.offsetX
            var offsetY = this.offsetY
            if (currentCanvasSize != IntegerSize.Zero && currentCanvasSize != value) {
                val sx = value.width.toFloat() / currentCanvasSize.width
                val sy = value.height.toFloat() / currentCanvasSize.height
                if (currentCanvasSize.aspectRatio < value.aspectRatio) {
                    scale *= minOf(sx, sy)
                    offsetX *= minOf(sx, sy)
                    offsetY *= minOf(sx, sy)
                } else {
                    scale /= minOf(sx, sy)
                    offsetX /= minOf(sx, sy)
                    offsetY /= minOf(sx, sy)
                }
            }

            return copy(
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                currentCanvasSize = value
            )
        }
    }
}

typealias DomainTextDecoration = LayerType.Text.Decoration

sealed interface LayerType {
    data class Text(
        val color: Int,
        val size: Float,
        val font: FontType?,
        val backgroundColor: Int,
        val text: String,
        val decorations: List<Decoration>,
        val outline: Outline?,
        val alignment: Alignment
    ) : LayerType {

        enum class Decoration {
            Bold, Italic, Underline, LineThrough
        }

        enum class Alignment {
            Start, Center, End
        }

        companion object {
            val Default by lazy {
                Text(
                    color = -16777216,
                    size = 0.2f,
                    font = null,
                    backgroundColor = 0,
                    text = "Text",
                    decorations = listOf(),
                    outline = null,
                    alignment = Alignment.Start,
                )
            }
        }
    }

    sealed class Picture(
        open val imageData: Any
    ) : LayerType {
        data class Image(override val imageData: Any) : Picture(imageData)
        data class Sticker(override val imageData: Any) : Picture(imageData)
    }
}