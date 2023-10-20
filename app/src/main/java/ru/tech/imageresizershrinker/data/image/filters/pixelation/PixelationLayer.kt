@file:Suppress("unused")

package ru.tech.imageresizershrinker.data.image.filters.pixelation


class PixelateLayer private constructor(var shape: Shape) {
    var enableDominantColor = false
    var resolution = 16f
    var size: Float? = null
    var alpha = 1f
    var offsetX = 0f
    var offsetY = 0f

    class Builder(shape: Shape) {
        private val layer: PixelateLayer

        init {
            layer = PixelateLayer(shape)
        }

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

        fun setShape(shape: Shape): Builder {
            layer.shape = shape
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

        fun build(): PixelateLayer {
            return layer
        }
    }

    enum class Shape {
        Circle,
        Diamond,
        Square
    }
}