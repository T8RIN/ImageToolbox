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
    coerceToBounds: Boolean = true
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
        coerceToBounds: Boolean = this.coerceToBounds
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
        coerceToBounds = coerceToBounds
    )

    var isActive by mutableStateOf(isActive)
        internal set

    var isInEditMode by mutableStateOf(false)
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
        if (_canvasSize.value != IntegerSize.Zero) {
            val sx = value.width.toFloat() / _canvasSize.value.width
            val sy = value.height.toFloat() / _canvasSize.value.height
            // Layer offsets live in canvas pixels, so restore them per axis when the
            // editor canvas changes after project import/reopen. Scaling the layer
            // itself here double-applies size changes because the content already
            // remeasures against the new canvas bounds.
            offset = Offset(
                x = offset.x * sx,
                y = offset.y * sy
            )
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

private fun Offset.rotateBy(
    angle: Float
): Offset {
    val angleInRadians = ROTATION_CONST * angle
    val newX = x * cos(angleInRadians) - y * sin(angleInRadians)
    val newY = x * sin(angleInRadians) + y * cos(angleInRadians)
    return Offset(newX, newY)
}

private const val ROTATION_CONST = (Math.PI / 180f).toFloat()
