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

package com.t8rin.imagetoolbox.feature.filters.data.utils.pixelation


internal class PixelationLayer private constructor(val shape: Shape) {
    var enableDominantColor = false
    var resolution = 16f
    var size: Float? = null
    var alpha = 1f
    var offsetX = 0f
    var offsetY = 0f

    class Builder(shape: Shape) {
        private val layer: PixelationLayer = PixelationLayer(shape)

        fun setResolution(resolution: Float): Builder {
            layer.resolution = resolution
            return this
        }

        fun setSize(size: Float): Builder {
            layer.size = size
            return this
        }

        fun setOffset(size: Float): Builder {
            layer.offsetX = size
            layer.offsetY = size
            return this
        }

        fun setAlpha(alpha: Float): Builder {
            layer.alpha = alpha
            return this
        }

        fun setEnableDominantColors(enable: Boolean): Builder {
            layer.enableDominantColor = enable
            return this
        }

        fun build(): PixelationLayer {
            return layer
        }
    }

    enum class Shape {
        Circle,
        Diamond,
        Square
    }
}