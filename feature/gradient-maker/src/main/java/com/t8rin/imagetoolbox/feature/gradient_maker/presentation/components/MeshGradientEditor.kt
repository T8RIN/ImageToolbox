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

package com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import com.t8rin.imagetoolbox.core.resources.icons.EditAlt
import com.t8rin.imagetoolbox.core.ui.theme.inverseByLuma
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorPickerSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.meshGradient
import com.t8rin.imagetoolbox.core.ui.widget.modifier.tappable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.saver.OffsetSaver
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
internal fun MeshGradientEditor(
    state: UiMeshGradientState,
    modifier: Modifier = Modifier
) {
    val selectedPoint = rememberSaveable(
        stateSaver = PairOffsetColorSaver
    ) { mutableStateOf(null) }

    val dragOffset = rememberSaveable(
        stateSaver = OffsetSaver
    ) { mutableStateOf(null) }

    val showColorPicker = rememberSaveable { mutableStateOf(false) }
    val isDragging = rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(ShapeDefaults.extraSmall)
            .transparencyChecker()
            .meshGradient(
                points = state.points,
                resolutionX = state.resolutionX,
                resolutionY = state.resolutionY
            )
            .tappable { tapOffset ->
                val tappedPoint = state.points.flatten()
                    .firstOrNull { (offset, _) ->
                        Offset(offset.x * size.width, offset.y * size.height).getDistance(
                            tapOffset
                        ) < 60f
                    }

                showColorPicker.value = tappedPoint == selectedPoint.value

                selectedPoint.value = tappedPoint
                if (tappedPoint == null) dragOffset.value = null
            }
    ) {
        val painter = rememberVectorPainter(Icons.Rounded.EditAlt)

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (selectedPoint.value != null) {
                        Modifier.pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { startOffset ->
                                    val tappedPoint = state.points.flatten()
                                        .firstOrNull { (offset, _) ->
                                            Offset(
                                                offset.x * size.width,
                                                offset.y * size.height
                                            ).getDistance(startOffset) < 60f
                                        }

                                    if (tappedPoint != null) {
                                        selectedPoint.value = tappedPoint
                                        dragOffset.value = tappedPoint.first
                                        isDragging.value = true
                                        showColorPicker.value = false
                                    }
                                },
                                onDrag = { _, dragAmount ->
                                    selectedPoint.value?.let { (oldOffset, color) ->
                                        val newOffset = Offset(
                                            ((oldOffset.x * size.width + dragAmount.x) / size.width).coerceIn(
                                                0f,
                                                1f
                                            ),
                                            ((oldOffset.y * size.height + dragAmount.y) / size.height).coerceIn(
                                                0f,
                                                1f
                                            )
                                        )
                                        state.updatePointPosition(oldOffset, newOffset)
                                        selectedPoint.value = newOffset to color
                                    }
                                },
                                onDragEnd = {
                                    isDragging.value = false
                                }
                            )
                        }
                    } else Modifier
                )
        ) {
            state.points.flatten().forEach { (offset, color) ->
                val scaledOffset = Offset(offset.x * size.width, offset.y * size.height)
                val isSelected = selectedPoint.value?.first == offset
                val inverseColor = color.inverseByLuma()
                drawContext.canvas.apply {
                    drawCircle(
                        radius = if (isSelected) 32f else 27f,
                        center = scaledOffset,
                        paint = Paint().asFrameworkPaint().apply {
                            setShadowLayer(
                                if (isSelected) 36f else 31f,
                                0f,
                                0f,
                                Color.Black.copy(alpha = if (isSelected) .8f else .4f)
                                    .toArgb()
                            )
                        }.asComposePaint()
                    )
                }
                if (isSelected) {
                    drawCircle(
                        color = inverseColor,
                        radius = 40f,
                        center = scaledOffset
                    )
                }
                drawCircle(
                    color = color,
                    radius = if (isSelected) 35f else 30f,
                    center = scaledOffset
                )
                if (isSelected) {
                    translate(
                        scaledOffset.x - 25f,
                        scaledOffset.y - 25f
                    ) {
                        with(painter) {
                            draw(
                                size = Size(width = 50f, height = 50f),
                                colorFilter = ColorFilter.tint(inverseColor)
                            )
                        }
                    }
                }
            }
        }

        ColorPickerSheet(
            visible = showColorPicker.value,
            onDismiss = { showColorPicker.value = false },
            color = selectedPoint.value?.second,
            onColorSelected = { newColor ->
                selectedPoint.value?.let { (offset) ->
                    state.updatePointColor(offset, newColor)
                }
            },
            allowAlpha = true
        )
    }
}

private fun UiMeshGradientState.updatePointPosition(
    oldOffset: Offset,
    newOffset: Offset
) {
    var found = false
    points.replaceAll { row ->
        row.map {
            if (it.first == oldOffset && !found) {
                found = true

                newOffset to it.second
            } else {
                it
            }
        }
    }
}

private fun UiMeshGradientState.updatePointColor(
    offset: Offset,
    newColor: Color
) {
    var found = false

    points.replaceAll { row ->
        row.map {
            if (it.first == offset && !found) {
                found = true

                it.first to newColor
            } else {
                it
            }
        }
    }
}

private fun Offset.getDistance(other: Offset): Float {
    return sqrt((x - other.x).pow(2) + (y - other.y).pow(2))
}

private val PairOffsetColorSaver: Saver<Pair<Offset, Color>?, List<Float>> =
    object : Saver<Pair<Offset, Color>?, List<Float>> {
        override fun restore(value: List<Float>): Pair<Offset, Color>? {
            return if (value.size == 5) {
                val offset = Offset(value[0], value[1])
                val color = Color(value[2], value[3], value[4])
                offset to color
            } else {
                null
            }
        }

        override fun SaverScope.save(value: Pair<Offset, Color>?): List<Float> {
            return if (value == null) emptyList()
            else listOf(
                value.first.x, value.first.y,
                value.second.red, value.second.green, value.second.blue
            )
        }
    }