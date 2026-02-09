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

package com.t8rin.imagetoolbox.color_tools.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.smarttoolfactory.colordetector.parser.ColorNameParser
import com.smarttoolfactory.colordetector.util.ColorUtil
import com.smarttoolfactory.colordetector.util.HexUtil
import com.smarttoolfactory.colorpicker.util.HexVisualTransformation
import com.smarttoolfactory.colorpicker.util.hexRegexSingleChar
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import android.graphics.Color as AndroidColor

@Composable
fun ColorInfoDisplay(
    value: Color,
    onValueChange: (Color?) -> Unit,
    onCopy: (String) -> Unit,
    onLoseFocus: () -> Unit
) {
    var hexColor by remember(value) { mutableStateOf(value.toHex()) }
    var rgb by remember(value) { mutableStateOf(value.toRGB()) }
    var hsv by remember(value) { mutableStateOf(value.toHSVString()) }
    var hsl by remember(value) { mutableStateOf(value.toHSL()) }
    var cmyk by remember(value) { mutableStateOf(value.toCMYK()) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ColorEditableField(
                label = "HEX",
                value = hexColor.removePrefix("#"),
                onCopy = onCopy,
                visualTransformation = HexVisualTransformation(false),
                onValueChange = { newHex ->
                    val newHex = newHex.replace("#", "")

                    if (newHex.length <= 8) {
                        var validHex = true

                        for (index in newHex.indices) {
                            validHex =
                                hexRegexSingleChar.matches(newHex[index].toString())
                            if (!validHex) break
                        }

                        if (validHex) {
                            hexColor = "#${newHex.uppercase()}"
                            val color = newHex.toColor()
                            onValueChange(color)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                onLoseFocus = onLoseFocus
            )
            ColorEditableField(
                label = "RGB",
                value = rgb,
                onValueChange = { newRgb ->
                    rgb = newRgb
                    val color = rgbToColor(newRgb)
                    onValueChange(color)
                },
                onCopy = onCopy,
                modifier = Modifier.weight(1f),
                onLoseFocus = onLoseFocus
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ColorEditableField(
                label = "HSV",
                value = hsv,
                onValueChange = { newHsv ->
                    hsv = newHsv
                    val color = hsvToColor(newHsv)
                    onValueChange(color)
                },
                onCopy = onCopy,
                modifier = Modifier.weight(1f),
                onLoseFocus = onLoseFocus
            )
            ColorEditableField(
                label = "HSL",
                value = hsl,
                onValueChange = { newHsl ->
                    hsl = newHsl
                    val color = hslToColor(newHsl)
                    onValueChange(color)
                },
                onCopy = onCopy,
                modifier = Modifier.weight(1f),
                onLoseFocus = onLoseFocus
            )
        }

        ColorEditableField(
            label = "CMYK",
            value = cmyk,
            onValueChange = { newCmyk ->
                cmyk = newCmyk
                val color = cmykToColor(newCmyk)
                onValueChange(color)
            },
            onCopy = onCopy,
            modifier = Modifier.fillMaxWidth(),
            onLoseFocus = onLoseFocus
        )

        var name by remember {
            mutableStateOf(ColorNameParser.parseColorName(color = value))
        }

        var isFocused by remember { mutableStateOf(false) }

        LaunchedEffect(value, isFocused) {
            if (!isFocused) {
                delay(200)
                name = ColorNameParser.parseColorName(value)
            }
        }

        ColorEditableField(
            label = stringResource(R.string.name),
            value = name,
            onValueChange = { newName ->
                name = newName
                onValueChange(
                    ColorNameParser.parseColorFromNameSingle(newName)
                )
            },
            onCopy = onCopy,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            onLoseFocus = onLoseFocus
        )
    }
}

internal fun getFormattedColor(color: Color): String {
    return if (color.alpha == 1f) {
        ColorUtil.colorToHex(color)
    } else {
        ColorUtil.colorToHexAlpha(color)
    }.uppercase()
}

@Composable
private fun ColorEditableField(
    label: String,
    value: String,
    onLoseFocus: () -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier
) {
    RoundedTextField(
        modifier = modifier,
        value = value,
        visualTransformation = visualTransformation,
        onValueChange = onValueChange,
        onLoseFocusTransformation = {
            onLoseFocus()
            this
        },
        label = {
            Text(label)
        },
        singleLine = true,
        endIcon = {
            EnhancedIconButton(
                onClick = { onCopy(value) },
                forceMinimumInteractiveComponentSize = false,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = null
                )
            }
        }
    )
}

fun Color.toHex(): String {
    val red = (red * 255).roundToInt()
    val green = (green * 255).roundToInt()
    val blue = (blue * 255).roundToInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}

fun String.toColor(): Color? {
    return runCatching {
        HexUtil.hexToColor(this)
    }.getOrNull()
}

fun Color.toRGB(): String {
    val r = (red * 255).roundToInt()
    val g = (green * 255).roundToInt()
    val b = (blue * 255).roundToInt()
    return "$r, $g, $b"
}

fun rgbToColor(rgb: String): Color? = runCatching {
    val (r, g, b) = rgb.split(",").map { it.trim().toInt() }
    Color(r / 255f, g / 255f, b / 255f)
}.getOrNull()

fun Color.toHSVString(): String {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(this.toArgb(), hsv)
    return "${hsv[0].roundToInt()}, ${(hsv[1] * 100).roundToInt()}, ${(hsv[2] * 100).roundToInt()}"
}

fun hsvToColor(hsv: String): Color? = runCatching {
    val (h, s, v) = hsv.split(",")
        .map { it.trim().toInt() }
    val colorInt = AndroidColor.HSVToColor(floatArrayOf(h.toFloat(), s / 100f, v / 100f))
    Color(colorInt)
}.getOrNull()

fun Color.toHSL(): String {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    return "${hsl[0].roundToInt()}, ${(hsl[1] * 100).roundToInt()}, ${(hsl[2] * 100).roundToInt()}"
}

fun hslToColor(hsl: String): Color? = runCatching {
    val (h, s, l) = hsl.split(",")
        .map { it.trim().toInt() }
    val colorInt = ColorUtils.HSLToColor(floatArrayOf(h.toFloat(), s / 100f, l / 100f))
    Color(colorInt)
}.getOrNull()

fun Color.toCMYK(): String {
    val k = (1 - maxOf(red, green, blue))
    val c = ((1 - red - k) / (1 - k)).takeIf { it.isFinite() } ?: 0f
    val m = ((1 - green - k) / (1 - k)).takeIf { it.isFinite() } ?: 0f
    val y = ((1 - blue - k) / (1 - k)).takeIf { it.isFinite() } ?: 0f
    return "${(c * 100).roundToInt()}, ${(m * 100).roundToInt()}, ${(y * 100).roundToInt()}, ${(k * 100).roundToInt()}"
}

fun cmykToColor(cmyk: String): Color? = runCatching {
    val (c, m, y, k) = cmyk.split(",").map { it.trim().toInt() / 100f }
    val r = 255 * (1 - c) * (1 - k)
    val g = 255 * (1 - m) * (1 - k)
    val b = 255 * (1 - y) * (1 - k)
    Color(r / 255f, g / 255f, b / 255f)
}.getOrNull()