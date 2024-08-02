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

package ru.tech.imageresizershrinker.color_tools.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.smarttoolfactory.colordetector.util.HexUtil
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import kotlin.math.roundToInt
import android.graphics.Color as AndroidColor

@Composable
fun ColorInfoDisplay(
    value: Color,
    onValueChange: (Color?) -> Unit,
    onCopy: (String) -> Unit
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
                value = hexColor,
                onCopy = onCopy,
                onValueChange = { newHex ->
                    hexColor = newHex
                    val color = newHex.toColor()
                    onValueChange(color)
                },
                modifier = Modifier.weight(1f)
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
                modifier = Modifier.weight(1f)
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
                modifier = Modifier.weight(1f)
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
                modifier = Modifier.weight(1f)
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
            modifier = Modifier.fillMaxWidth(1f)
        )
    }
}

@Composable
private fun ColorEditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onCopy: (String) -> Unit,
    modifier: Modifier
) {
    RoundedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        singleLine = true
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

fun rgbToColor(rgb: String): Color? {
    return try {
        val (r, g, b) = rgb.split(",").map { it.trim().toInt() }
        Color(r / 255f, g / 255f, b / 255f)
    } catch (e: Throwable) {
        null
    }
}

fun Color.toHSVString(): String {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(this.toArgb(), hsv)
    return "${hsv[0].roundToInt()}°, ${(hsv[1] * 100).roundToInt()}%, ${(hsv[2] * 100).roundToInt()}%"
}

fun hsvToColor(hsv: String): Color? {
    return try {
        val (h, s, v) = hsv.split(",")
            .map { it.trim().replace("°", "").replace("%", "").toFloat() / 100 }
        val colorInt = AndroidColor.HSVToColor(floatArrayOf(h * 360, s, v))
        Color(colorInt)
    } catch (e: Throwable) {
        null
    }
}

fun Color.toHSL(): String {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    return "${hsl[0].roundToInt()}°, ${(hsl[1] * 100).roundToInt()}%, ${(hsl[2] * 100).roundToInt()}%"
}

fun hslToColor(hsl: String): Color? {
    return try {
        val (h, s, l) = hsl.split(",")
            .map { it.trim().replace("°", "").replace("%", "").toFloat() / 100 }
        val colorInt = ColorUtils.HSLToColor(floatArrayOf(h * 360, s, l))
        Color(colorInt)
    } catch (e: Throwable) {
        null
    }
}

fun Color.toCMYK(): String {
    val r = red * 255
    val g = green * 255
    val b = blue * 255
    val k = (1 - maxOf(r, g, b) / 255).takeIf { it > 1f } ?: 2f
    val c = (1 - r / 255 - k) / (1 - k)
    val m = (1 - g / 255 - k) / (1 - k)
    val y = (1 - b / 255 - k) / (1 - k)
    return "${(c * 100).roundToInt()}%, ${(m * 100).roundToInt()}%, ${(y * 100).roundToInt()}, ${(k * 100).roundToInt()}%"
}

fun cmykToColor(cmyk: String): Color? {
    return try {
        val (c, m, y, k) = cmyk.split(",").map { it.trim().toFloat() / 100 }
        val r = 255 * (1 - c) * (1 - k)
        val g = 255 * (1 - m) * (1 - k)
        val b = 255 * (1 - y) * (1 - k)
        Color(r / 255f, g / 255f, b / 255f)
    } catch (e: Throwable) {
        null
    }
}