package ru.tech.imageresizershrinker.feature.markup_layers.presentation.components

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastCoerceIn
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

class EditBoxState(
    scale: Float = 1f,
    rotation: Float = 0f,
    offset: Offset = Offset.Companion.Zero,
    isActive: Boolean = false
) {
    var isActive by mutableStateOf(isActive)
        internal set

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
        scale = (scale * zoomChange).fastCoerceIn(0.5f, 10f)
        val panChange = (offsetChange * scale).rotateBy(rotation)

        val extraWidth = (parentMaxWidth - contentSize.width * scale).absoluteValue
        val extraHeight = (parentMaxHeight - contentSize.height * scale).absoluteValue

        val maxX = extraWidth / 2
        val maxY = extraHeight / 2

        offset = Offset(
            x = (offset.x + panChange.x).coerceIn(-maxX, maxX),
            y = (offset.y + panChange.y).coerceIn(-maxY, maxY),
        )
    }

    var scale by mutableFloatStateOf(scale)
        internal set

    var rotation by mutableFloatStateOf(rotation)
        internal set

    var offset by mutableStateOf(offset)
        internal set

    private val IntSize.aspect: Float get() = width / height.toFloat()

    private val _canvasSize = mutableStateOf(IntSize.Companion.Zero)

    var canvasSize: IntSize
        get() = _canvasSize.value
        set(value) {
            if (_canvasSize.value != IntSize.Companion.Zero && _canvasSize.value != value) {
                val sx = value.width.toFloat() / _canvasSize.value.width
                val sy = value.height.toFloat() / _canvasSize.value.height
                if (_canvasSize.value.aspect < value.aspect) {
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

private fun Offset.rotateBy(
    angle: Float
): Offset {
    val angleInRadians = ROTATION_CONST * angle
    val newX = x * cos(angleInRadians) - y * sin(angleInRadians)
    val newY = x * sin(angleInRadians) + y * cos(angleInRadians)
    return Offset(newX, newY)
}

private const val ROTATION_CONST = (Math.PI / 180f).toFloat()