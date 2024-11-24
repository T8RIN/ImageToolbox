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

package ru.tech.imageresizershrinker.feature.markup_layers.domain

import ru.tech.imageresizershrinker.core.domain.model.IntegerSize

data class MarkupLayer(
    val type: LayerType,
    val position: LayerPosition
)

data class LayerPosition(
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val currentCanvasSize: IntegerSize
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

sealed interface LayerType {
    data class Text(
        val color: Int,
        val style: Int,
        val size: Float,
        val font: Int,
        val backgroundColor: Int,
        val text: String,
    ) : LayerType {
        companion object {
            val Default by lazy {
                Text(
                    color = -16777216,
                    style = 0,
                    size = 0.1f,
                    font = 0,
                    backgroundColor = 0,
                    text = "Text"
                )
            }
        }
    }

    data class Image(
        val imageData: Any
    ) : LayerType {
        companion object {
            val Default by lazy {
                Image(
                    imageData = "file:///android_asset/svg/emotions/aasparkles.svg"
                )
            }
        }
    }
}