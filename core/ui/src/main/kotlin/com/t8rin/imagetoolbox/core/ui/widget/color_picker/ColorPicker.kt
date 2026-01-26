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
package com.t8rin.imagetoolbox.core.ui.widget.color_picker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.smarttoolfactory.colordetector.util.ColorUtil
import com.smarttoolfactory.colorpicker.selector.SelectorRectSaturationValueHSV
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker

@Composable
fun ColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    containerColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
    hueSliderConfig: HueSliderThumbConfig = HueSliderThumbConfig.Default,
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(0f) }
    var value by remember { mutableFloatStateOf(0f) }
    var alpha by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(selectedColor) {
        val (h, s, v) = ColorUtil.colorToHSV(selectedColor)
        hue = h
        saturation = s
        value = v
        alpha = selectedColor.alpha
    }

    LaunchedEffect(hue, saturation, value, alpha) {
        onColorSelected(
            Color.hsv(
                hue = hue,
                saturation = saturation,
                value = value,
                alpha = alpha
            )
        )
    }

    Row(modifier = modifier) {
        SelectorRectSaturationValueHSV(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .container(
                    shape = ShapeDefaults.pressed,
                    resultPadding = 0.dp,
                    color = containerColor,
                    clip = false
                ),
            hue = hue,
            saturation = saturation,
            value = value
        ) { s, v ->
            saturation = s
            value = v
        }

        Spacer(modifier = Modifier.width(8.dp))

        HueSlider(
            hue = hue,
            onHueSelected = { hue = it },
            thumbConfig = hueSliderConfig,
            modifier = Modifier.width(36.dp),
        )

        if (hueSliderConfig.withAlpha) {
            Spacer(modifier = Modifier.width(8.dp))

            AlphaSlider(
                alpha = alpha,
                onAlphaSelected = { alpha = it },
                color = selectedColor,
                thumbConfig = hueSliderConfig,
                modifier = Modifier.width(36.dp),
            )
        }
    }
}

@Immutable
data class HueSliderThumbConfig(
    val height: Dp = 12.dp,
    val color: Color = Color.White,
    val borderSize: Dp = 2.dp,
    val borderRadius: Float = 100f,
    val withAlpha: Boolean = false
) {
    companion object {
        val Default = HueSliderThumbConfig()
    }
}

@Composable
private fun HueSlider(
    hue: Float,
    onHueSelected: (Float) -> Unit,
    modifier: Modifier = Modifier,
    thumbConfig: HueSliderThumbConfig = HueSliderThumbConfig.Default,
) {
    var sliderSize by remember { mutableStateOf(Size.Zero) }
    val thumbHeightPx = with(LocalDensity.current) { thumbConfig.height.toPx() }

    fun updateThumbByOffset(offsetY: Float) {
        if (sliderSize.height <= 0f) return

        val maxThumbY = sliderSize.height - thumbHeightPx
        val clampedY = offsetY.coerceIn(0f, maxThumbY)

        val newHue = ((clampedY / maxThumbY) * 359f).coerceIn(0f, 359f)
        onHueSelected(newHue)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .onSizeChanged { sliderSize = it.toSize() }
            .pointerInput(Unit) {
                detectTapGestures { offset -> updateThumbByOffset(offset.y) }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ -> updateThumbByOffset(change.position.y) }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = thumbConfig.borderSize)
                .clip(ShapeDefaults.extraSmall)
        ) {
            drawRect(brush = Brush.verticalGradient(Color.colorList))
        }

        if (sliderSize.height > 0f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val thumbY = (hue / 359f) * (sliderSize.height - thumbHeightPx)
                drawRoundRect(
                    color = thumbConfig.color,
                    topLeft = Offset(
                        x = thumbConfig.borderSize.toPx() / 2,
                        y = thumbY
                    ),
                    size = Size(
                        width = sliderSize.width - thumbConfig.borderSize.toPx(),
                        height = thumbHeightPx
                    ),
                    style = Stroke(width = thumbConfig.borderSize.toPx()),
                    cornerRadius = CornerRadius(
                        thumbConfig.borderRadius,
                        thumbConfig.borderRadius
                    )
                )
            }
        }
    }
}

@Composable
private fun AlphaSlider(
    onAlphaSelected: (Float) -> Unit,
    modifier: Modifier = Modifier,
    alpha: Float,
    color: Color,
    thumbConfig: HueSliderThumbConfig = HueSliderThumbConfig.Default,
) {
    var sliderSize by remember { mutableStateOf(Size.Zero) }
    val density = LocalDensity.current

    val thumbHeightPx = with(density) { thumbConfig.height.toPx() }
    val updateAlpha by rememberUpdatedState(onAlphaSelected)

    fun onThumbPositionChange(newOffset: Offset) {
        if (sliderSize.height <= 0f) return

        val maxThumbY = sliderSize.height - thumbHeightPx
        val clampedY = newOffset.y.coerceIn(0f, maxThumbY)

        val newAlpha = ((maxThumbY - clampedY) / maxThumbY).coerceIn(0f, 1f)
        updateAlpha(newAlpha)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .onSizeChanged { sliderSize = it.toSize() }
            .pointerInput(Unit) {
                detectTapGestures { offset -> onThumbPositionChange(offset) }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    onThumbPositionChange(change.position)
                    change.consume()
                }
            }
    ) {
        val check = if (sliderSize.width > 0f) {
            with(density) { sliderSize.width.toDp() / 3.3f }
        } else {
            10.dp
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = thumbConfig.borderSize)
                .clip(ShapeDefaults.extraSmall)
                .transparencyChecker(
                    checkerWidth = check,
                    checkerHeight = check
                )
        ) {
            drawRect(
                Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 1f),
                        color.copy(alpha = 0f)
                    )
                )
            )
        }

        if (!sliderSize.isEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val thumbY = (1f - alpha) * (sliderSize.height - thumbHeightPx)
                drawRoundRect(
                    color = thumbConfig.color,
                    topLeft = Offset(
                        x = thumbConfig.borderSize.toPx() / 2f,
                        y = thumbY
                    ),
                    size = Size(
                        width = sliderSize.width - thumbConfig.borderSize.toPx(),
                        height = thumbHeightPx
                    ),
                    style = Stroke(width = thumbConfig.borderSize.toPx()),
                    cornerRadius = CornerRadius(thumbConfig.borderRadius, thumbConfig.borderRadius)
                )
            }
        }
    }
}

private val Color.Companion.colorList by lazy {
    IntArray(359) { it }.map { deg ->
        Color.hsv(
            hue = deg.toFloat(),
            saturation = 1f,
            value = 0.8f
        )
    }
}