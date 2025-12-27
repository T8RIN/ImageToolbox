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

@file:Suppress("unused")

package com.t8rin.imagetoolbox.feature.filters.data.utils.pixelation.tool


@ConsistentCopyVisibility
internal data class PixelationLayer private constructor(
    val shape: Shape,
    val enableDominantColor: Boolean = false,
    val resolution: Float = 16f,
    val size: Float? = null,
    val alpha: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f
) {
    class Builder(shape: Shape) {
        private var layer: PixelationLayer = PixelationLayer(shape)

        private inline fun mutate(
            action: PixelationLayer.() -> PixelationLayer
        ): Builder = apply { layer = layer.action() }

        fun setResolution(resolution: Float): Builder = mutate {
            copy(resolution = resolution)
        }

        fun setSize(size: Float): Builder = mutate {
            copy(
                size = size
            )
        }

        fun setOffset(size: Float): Builder = mutate {
            copy(
                offsetX = size,
                offsetY = size
            )
        }

        fun setAlpha(alpha: Float): Builder = mutate {
            copy(
                alpha = alpha
            )
        }

        fun setEnableDominantColors(enable: Boolean): Builder = mutate {
            copy(
                enableDominantColor = enable
            )
        }

        fun build(): PixelationLayer = layer
    }

    enum class Shape {
        Circle,
        Diamond,
        Square
    }
}