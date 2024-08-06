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

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Analogous
import ru.tech.imageresizershrinker.core.resources.icons.AnalogousComplementary
import ru.tech.imageresizershrinker.core.resources.icons.Complementary
import ru.tech.imageresizershrinker.core.resources.icons.SplitComplementary
import ru.tech.imageresizershrinker.core.resources.icons.SquareHarmony
import ru.tech.imageresizershrinker.core.resources.icons.Tetradic
import ru.tech.imageresizershrinker.core.resources.icons.Triadic
import ru.tech.imageresizershrinker.core.ui.theme.toColor
import android.graphics.Color as AndroidColor

fun Color.applyHarmony(
    type: HarmonyType
): List<Color> {
    val (h, s, v) = toHSV()
    return when (type) {
        HarmonyType.COMPLEMENTARY -> listOf(
            hsvToColor(h, s, v),
            hsvToColor((h + 180) % 360, s, v)
        )

        HarmonyType.ANALOGOUS -> listOf(
            hsvToColor(h, s, v),
            hsvToColor((h + 30) % 360, s, v),
            hsvToColor((h - 30 + 360) % 360, s, v)
        )

        HarmonyType.ANALOGOUS_COMPLEMENTARY -> listOf(
            hsvToColor(h, s, v),
            hsvToColor((h + 30) % 360, s, v),
            hsvToColor((h - 30 + 360) % 360, s, v),
            hsvToColor((h + 180) % 360, s, v)
        )

        HarmonyType.TRIADIC -> listOf(
            hsvToColor(h, s, v),
            hsvToColor((h + 120) % 360, s, v),
            hsvToColor((h - 120 + 360) % 360, s, v)
        )

        HarmonyType.SPLIT_COMPLEMENTARY -> listOf(
            hsvToColor(h, s, v),
            hsvToColor((h + 150) % 360, s, v),
            hsvToColor((h - 150 + 360) % 360, s, v)
        )

        HarmonyType.TETRADIC -> listOf(
            hsvToColor(h, s, v),
            hsvToColor((h + 30) % 360, s, v),
            hsvToColor((h + 180) % 360, s, v),
            hsvToColor((h + 210) % 360, s, v)
        )

        HarmonyType.SQUARE -> listOf(
            hsvToColor(h, s, v),
            hsvToColor((h + 90) % 360, s, v),
            hsvToColor((h + 180) % 360, s, v),
            hsvToColor((h + 270) % 360, s, v)
        )
    }.map { it.copy(alpha) }
}

enum class HarmonyType {
    COMPLEMENTARY,
    ANALOGOUS,
    ANALOGOUS_COMPLEMENTARY,
    TRIADIC,
    SPLIT_COMPLEMENTARY,
    TETRADIC,
    SQUARE
}

@Composable
fun HarmonyType.title(): String = when (this) {
    HarmonyType.COMPLEMENTARY -> stringResource(R.string.harmony_complementary)
    HarmonyType.ANALOGOUS -> stringResource(R.string.harmony_analogous)
    HarmonyType.TRIADIC -> stringResource(R.string.harmony_triadic)
    HarmonyType.SPLIT_COMPLEMENTARY -> stringResource(R.string.harmony_split_complementary)
    HarmonyType.TETRADIC -> stringResource(R.string.harmony_tetradic)
    HarmonyType.SQUARE -> stringResource(R.string.harmony_square)
    HarmonyType.ANALOGOUS_COMPLEMENTARY -> stringResource(R.string.harmony_analogous_complementary)
}

fun HarmonyType.icon(): ImageVector = when (this) {
    HarmonyType.COMPLEMENTARY -> Icons.Filled.Complementary
    HarmonyType.ANALOGOUS -> Icons.Filled.Analogous
    HarmonyType.ANALOGOUS_COMPLEMENTARY -> Icons.Filled.AnalogousComplementary
    HarmonyType.TRIADIC -> Icons.Filled.Triadic
    HarmonyType.SPLIT_COMPLEMENTARY -> Icons.Filled.SplitComplementary
    HarmonyType.TETRADIC -> Icons.Filled.Tetradic
    HarmonyType.SQUARE -> Icons.Filled.SquareHarmony
}

fun Color.toHSV(): FloatArray {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(toArgb(), hsv)
    return hsv
}

fun hsvToColor(
    h: Float,
    s: Float,
    v: Float
): Color {
    return AndroidColor.HSVToColor(floatArrayOf(h, s, v)).toColor()
}