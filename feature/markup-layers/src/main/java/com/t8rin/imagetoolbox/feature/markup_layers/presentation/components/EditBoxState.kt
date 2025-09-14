/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastCoerceIn
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

class EditBoxState(
    scale: Float = 1f,
    rotation: Float = 0f,
    offset: Offset = Offset.Zero,
    alpha: Float = 1f,
    isActive: Boolean = false,
    canvasSize: IntegerSize = IntegerSize.Zero,
    isVisible: Boolean = true,
    coerceToBounds: Boolean = true
) {
    fun copy(
        scale: Float = this.scale,
        rotation: Float = this.rotation,
        offset: Offset = this.offset,
        alpha: Float = this.alpha,
        isActive: Boolean = this.isActive,
        canvasSize: IntegerSize = this.canvasSize,
        isVisible: Boolean = this.isVisible,
        coerceToBounds: Boolean = this.coerceToBounds
    ): EditBoxState = EditBoxState(
        scale = scale,
        rotation = rotation,
        offset = offset,
        alpha = alpha,
        isActive = isActive,
        canvasSize = canvasSize,
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
        zoomChange: Float,
        offsetChange: Offset,
        rotationChange: Float
    ) {
        rotation += rotationChange
        scale = (scale * zoomChange).fastCoerceIn(0.3f, 10f)
        val panChange = (offsetChange * scale).rotateBy(rotation)

        val rotatedSize = contentSize.rotateBy(rotation)

        val extraWidth = (parentMaxWidth - rotatedSize.width * scale).absoluteValue
        val extraHeight = (parentMaxHeight - rotatedSize.height * scale).absoluteValue

        val maxX = extraWidth / 2 // + contentSize.width * scale / 2
        val maxY = extraHeight / 2 // + contentSize.height * scale / 2

        offset = Offset(
            x = (offset.x + panChange.x).coerceIn(-maxX, maxX, coerceToBounds),
            y = (offset.y + panChange.y).coerceIn(-maxY, maxY, coerceToBounds),
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

    var offset by mutableStateOf(offset)
        internal set

    var alpha by mutableFloatStateOf(alpha)
        internal set

    var coerceToBounds by mutableStateOf(coerceToBounds)
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
            if (abs(_canvasSize.value.aspectRatio - value.aspectRatio) < 0.01) {
                offset *= minOf(sx, sy)
            } else if (_canvasSize.value.aspectRatio < value.aspectRatio) {
                scale *= minOf(sx, sy)
                offset *= minOf(sx, sy)
            } else {
                scale /= minOf(sx, sy)
                offset /= minOf(sx, sy)
            }
        }
        _canvasSize.value = value
    }
}

internal fun DpSize.rotateBy(
    degrees: Float,
    density: Density
): DpSize = with(density) {
    IntSize(width.roundToPx(), height.roundToPx()).rotateBy(degrees).run {
        DpSize(width.toDp(), height.toDp())
    }
}

private fun IntSize.rotateBy(degrees: Float): IntSize {
    var normalizedDegrees = degrees % 180
    if (normalizedDegrees < 0) {
        normalizedDegrees += 180
    }
    var currentSize = this
    if (normalizedDegrees >= 90) {
        currentSize = IntSize(height, width)
        normalizedDegrees -= 90
    }
    if (normalizedDegrees == 0f) {
        return currentSize
    }
    val radians = Math.toRadians(normalizedDegrees.toDouble())
    val width = ceil(currentSize.width * cos(radians) + currentSize.height * sin(radians)).toInt()
    val height = ceil(currentSize.width * sin(radians) + currentSize.height * cos(radians)).toInt()
    return IntSize(width, height)
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