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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastCoerceIn
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class EditBoxState(
    scale: Float = 1f,
    rotation: Float = 0f,
    isFlippedHorizontally: Boolean = false,
    isFlippedVertically: Boolean = false,
    offset: Offset = Offset.Zero,
    alpha: Float = 1f,
    isActive: Boolean = false,
    canvasSize: IntegerSize = IntegerSize.Zero,
    contentSize: IntSize = IntSize.Zero,
    isVisible: Boolean = true,
    coerceToBounds: Boolean = true,
    isInEditMode: Boolean = false,
) {
    fun copy(
        scale: Float = this.scale,
        rotation: Float = this.rotation,
        isFlippedHorizontally: Boolean = this.isFlippedHorizontally,
        isFlippedVertically: Boolean = this.isFlippedVertically,
        offset: Offset = this.offset,
        alpha: Float = this.alpha,
        isActive: Boolean = this.isActive,
        canvasSize: IntegerSize = this.canvasSize,
        contentSize: IntSize = this.contentSize,
        isVisible: Boolean = this.isVisible,
        coerceToBounds: Boolean = this.coerceToBounds,
        isInEditMode: Boolean = this.isInEditMode,
    ): EditBoxState = EditBoxState(
        scale = scale,
        rotation = rotation,
        isFlippedHorizontally = isFlippedHorizontally,
        isFlippedVertically = isFlippedVertically,
        offset = offset,
        alpha = alpha,
        isActive = isActive,
        canvasSize = canvasSize,
        contentSize = contentSize,
        isVisible = isVisible,
        coerceToBounds = coerceToBounds,
        isInEditMode = isInEditMode
    )

    var isActive by mutableStateOf(isActive)
        internal set

    var isInEditMode by mutableStateOf(isInEditMode)
        internal set

    private val _isVisible = mutableStateOf(isVisible)

    var isVisible: Boolean
        get() = _isVisible.value
        internal set(value) {
            if (!value) {
                isActive = false
            }
            _isVisible.value = value
        }

    fun activate() {
        isActive = true
    }

    fun deactivate() {
        isActive = false
    }

    internal fun applyChanges(
        parentMaxWidth: Int,
        parentMaxHeight: Int,
        contentSize: IntSize,
        cornerRadiusPercent: Int,
        zoomChange: Float,
        offsetChange: Offset,
        rotationChange: Float
    ) {
        applyChangeSet(
            parentMaxWidth = parentMaxWidth,
            parentMaxHeight = parentMaxHeight,
            contentSize = contentSize,
            cornerRadiusPercent = cornerRadiusPercent,
            zoomChange = zoomChange,
            offsetChange = (offsetChange * scale).rotateBy(rotation),
            rotationChange = rotationChange
        )
    }

    internal fun applyGlobalChanges(
        parentMaxWidth: Int,
        parentMaxHeight: Int,
        contentSize: IntSize,
        cornerRadiusPercent: Int,
        zoomChange: Float,
        offsetChange: Offset,
        rotationChange: Float
    ) {
        applyChangeSet(
            parentMaxWidth = parentMaxWidth,
            parentMaxHeight = parentMaxHeight,
            contentSize = contentSize,
            cornerRadiusPercent = cornerRadiusPercent,
            zoomChange = zoomChange,
            offsetChange = offsetChange,
            rotationChange = rotationChange
        )
    }

    private fun applyChangeSet(
        parentMaxWidth: Int,
        parentMaxHeight: Int,
        contentSize: IntSize,
        cornerRadiusPercent: Int,
        zoomChange: Float,
        offsetChange: Offset,
        rotationChange: Float
    ) {
        rotation += rotationChange
        scale = (scale * zoomChange).fastCoerceIn(0.3f, 10f)

        val halfExtents = contentSize.rotatedHalfExtents(
            degrees = rotation,
            cornerRadiusPercent = cornerRadiusPercent
        )
        val halfParentWidth = parentMaxWidth / 2f
        val halfParentHeight = parentMaxHeight / 2f
        val maxX = (halfParentWidth - halfExtents.x * scale).absoluteValue
        val maxY = (halfParentHeight - halfExtents.y * scale).absoluteValue

        offset = Offset(
            x = (offset.x + offsetChange.x).coerceIn(-maxX, maxX, coerceToBounds),
            y = (offset.y + offsetChange.y).coerceIn(-maxY, maxY, coerceToBounds),
        )
    }

    private fun Float.coerceIn(
        minimumValue: Float,
        maximumValue: Float,
        enable: Boolean
    ) = if (enable) coerceIn(minimumValue, maximumValue) else this

    var scale by mutableFloatStateOf(scale)
        internal set

    var rotation by mutableFloatStateOf(rotation)
        internal set

    var isFlippedHorizontally by mutableStateOf(isFlippedHorizontally)
        internal set

    var isFlippedVertically by mutableStateOf(isFlippedVertically)
        internal set

    var offset by mutableStateOf(offset)
        internal set

    var alpha by mutableFloatStateOf(alpha)
        internal set

    var coerceToBounds by mutableStateOf(coerceToBounds)
        internal set

    var contentSize by mutableStateOf(contentSize)
        internal set

    private val _canvasSize = mutableStateOf(IntegerSize.Zero)

    init {
        adjustByCanvasSize(canvasSize)
    }

    var canvasSize: IntegerSize
        get() = _canvasSize.value
        set(value) {
            adjustByCanvasSize(value)
        }

    private fun adjustByCanvasSize(value: IntegerSize) {
        val previousCanvasSize = _canvasSize.value
        if (previousCanvasSize != IntegerSize.Zero) {
            val sx = value.width.toFloat() / previousCanvasSize.width
            val sy = value.height.toFloat() / previousCanvasSize.height

            offset = Offset(
                x = offset.x * sx,
                y = offset.y * sy
            )

            // Layers whose measured content did not depend on the canvas bounds
            // need an explicit scale adjustment to preserve their visual size
            // relative to the preview after canvas resize. Layers already capped
            // by `sizeIn(max = canvas / 2)` will remeasure on their own.
            if (contentSize.isSpecified() && !contentSize.isBoundedByCanvas(previousCanvasSize)) {
                scale = (scale * min(sx, sy)).fastCoerceIn(0.3f, 10f)
            }
        }
        _canvasSize.value = value
    }

    internal fun normalizedPosition(
        cornerRadiusPercent: Int
    ): Offset? {
        val contentSize = contentSize
        val canvasWidth = canvasSize.width.takeIf { it > 0 } ?: return null
        val canvasHeight = canvasSize.height.takeIf { it > 0 } ?: return null

        if (contentSize.width <= 0 || contentSize.height <= 0) return null

        val halfExtents = contentSize.rotatedHalfExtents(
            degrees = rotation,
            cornerRadiusPercent = cornerRadiusPercent
        )

        val halfWidth = halfExtents.x * scale
        val halfHeight = halfExtents.y * scale
        val layerWidth = halfWidth * 2f
        val layerHeight = halfHeight * 2f
        val left = canvasWidth / 2f + offset.x - halfWidth
        val top = canvasHeight / 2f + offset.y - halfHeight

        return Offset(
            x = left.normalizeEdgeAware(
                axisSize = canvasWidth.toFloat(),
                layerSize = layerWidth
            ),
            y = top.normalizeEdgeAware(
                axisSize = canvasHeight.toFloat(),
                layerSize = layerHeight
            )
        )
    }

    internal fun setNormalizedPosition(
        x: Float? = null,
        y: Float? = null,
        cornerRadiusPercent: Int
    ) {
        if (x == null && y == null) return

        val contentSize = contentSize
        val canvasWidth = canvasSize.width.takeIf { it > 0 } ?: return
        val canvasHeight = canvasSize.height.takeIf { it > 0 } ?: return

        if (contentSize.width <= 0 || contentSize.height <= 0) return

        val halfExtents = contentSize.rotatedHalfExtents(
            degrees = rotation,
            cornerRadiusPercent = cornerRadiusPercent
        )

        val halfWidth = halfExtents.x * scale
        val halfHeight = halfExtents.y * scale
        val layerWidth = halfWidth * 2f
        val layerHeight = halfHeight * 2f
        val currentLeft = canvasWidth / 2f + offset.x - halfWidth
        val currentTop = canvasHeight / 2f + offset.y - halfHeight

        val targetLeft = x?.denormalizeEdgeAware(
            axisSize = canvasWidth.toFloat(),
            layerSize = layerWidth
        ) ?: currentLeft
        val targetTop = y?.denormalizeEdgeAware(
            axisSize = canvasHeight.toFloat(),
            layerSize = layerHeight
        ) ?: currentTop
        val targetOffset = Offset(
            x = targetLeft - canvasWidth / 2f + halfWidth,
            y = targetTop - canvasHeight / 2f + halfHeight
        )

        applyGlobalChanges(
            parentMaxWidth = canvasWidth,
            parentMaxHeight = canvasHeight,
            contentSize = contentSize,
            cornerRadiusPercent = cornerRadiusPercent,
            zoomChange = 1f,
            offsetChange = Offset(
                x = targetOffset.x - offset.x,
                y = targetOffset.y - offset.y
            ),
            rotationChange = 0f
        )
    }

    internal fun setScalePrecisely(
        targetScale: Float,
        cornerRadiusPercent: Int
    ) {
        val contentSize = contentSize
        val scale = targetScale.fastCoerceIn(0.3f, 10f)

        if (contentSize.width <= 0 || contentSize.height <= 0) {
            this.scale = scale
            return
        }

        val canvasWidth = canvasSize.width.takeIf { it > 0 } ?: contentSize.width
        val canvasHeight = canvasSize.height.takeIf { it > 0 } ?: contentSize.height

        applyGlobalChanges(
            parentMaxWidth = canvasWidth,
            parentMaxHeight = canvasHeight,
            contentSize = contentSize,
            cornerRadiusPercent = cornerRadiusPercent,
            zoomChange = scale / this.scale.coerceAtLeast(0.0001f),
            offsetChange = Offset.Zero,
            rotationChange = 0f
        )
    }
}

private fun Float.normalizeEdgeAware(
    axisSize: Float,
    layerSize: Float
): Float {
    val safeAxisSize = axisSize.coerceAtLeast(1f)
    val safeLayerSize = layerSize.coerceAtLeast(1f)
    val maxInsideStart = safeAxisSize - safeLayerSize

    return when {
        maxInsideStart > 0f -> when {
            this < 0f -> this / safeLayerSize
            this + safeLayerSize > safeAxisSize -> 1f + (this - maxInsideStart) / safeLayerSize
            else -> this / maxInsideStart
        }

        maxInsideStart == 0f -> when {
            this < 0f -> this / safeLayerSize
            this > 0f -> 1f + this / safeLayerSize
            else -> 0f
        }

        else -> when {
            this > 0f -> -this / safeLayerSize
            this < maxInsideStart -> 1f + (maxInsideStart - this) / safeLayerSize
            else -> this / maxInsideStart
        }
    }
}

private fun Float.denormalizeEdgeAware(
    axisSize: Float,
    layerSize: Float
): Float {
    val safeAxisSize = axisSize.coerceAtLeast(1f)
    val safeLayerSize = layerSize.coerceAtLeast(1f)
    val maxInsideStart = safeAxisSize - safeLayerSize

    return when {
        maxInsideStart > 0f -> when {
            this < 0f -> this * safeLayerSize
            this > 1f -> maxInsideStart + (this - 1f) * safeLayerSize
            else -> this * maxInsideStart
        }

        maxInsideStart == 0f -> when {
            this < 0f -> this * safeLayerSize
            this > 1f -> (this - 1f) * safeLayerSize
            else -> 0f
        }

        else -> when {
            this < 0f -> -this * safeLayerSize
            this > 1f -> maxInsideStart - (this - 1f) * safeLayerSize
            else -> this * maxInsideStart
        }
    }
}

private fun IntSize.rotatedHalfExtents(
    degrees: Float,
    cornerRadiusPercent: Int
): Offset {
    val halfWidth = width / 2f
    val halfHeight = height / 2f

    val cornerRadiusPx = (
            min(width, height) *
                    (cornerRadiusPercent.coerceIn(0, 50) / 100f)
            ).coerceIn(0f, min(halfWidth, halfHeight))

    // Rounded rectangle can be represented as an inner rectangle expanded by a circle.
    // This gives accurate support extents for collision checks in any rotation.
    val innerHalfWidth = max(0f, halfWidth - cornerRadiusPx)
    val innerHalfHeight = max(0f, halfHeight - cornerRadiusPx)

    val radians = Math.toRadians(degrees.toDouble())
    val cos = abs(cos(radians)).toFloat()
    val sin = abs(sin(radians)).toFloat()

    return Offset(
        x = innerHalfWidth * cos + innerHalfHeight * sin + cornerRadiusPx,
        y = innerHalfWidth * sin + innerHalfHeight * cos + cornerRadiusPx
    )
}

private fun IntSize.isSpecified(): Boolean = width > 0 && height > 0

private fun IntSize.isBoundedByCanvas(
    canvasSize: IntegerSize,
    tolerancePx: Int = 1
): Boolean {
    val maxWidth = canvasSize.width / 2
    val maxHeight = canvasSize.height / 2

    return width >= maxWidth - tolerancePx || height >= maxHeight - tolerancePx
}

private fun Offset.rotateBy(
    angle: Float
): Offset {
    val angleInRadians = ROTATION_CONST * angle
    val newX = x * cos(angleInRadians) - y * sin(angleInRadians)
    val newY = x * sin(angleInRadians) + y * cos(angleInRadians)
    return Offset(newX, newY)
}

private const val ROTATION_CONST = (Math.PI / 180f).toFloat()
