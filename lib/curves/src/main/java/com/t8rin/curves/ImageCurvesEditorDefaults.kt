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

package com.t8rin.curves

import androidx.annotation.FloatRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme

object ImageCurvesEditorDefaults {

    val Colors: ImageCurvesEditorColors
        @Composable
        get() {
            val primary = MaterialTheme.colorScheme.primary
            val lumaCurveColor = takeColorFromScheme { onSurface }
            val gridLineColor = takeColorFromScheme { isNightMode ->
                onSurface.copy(alpha = if (isNightMode) 0.26f else 0.38f)
            }
            val referenceLineColor = takeColorFromScheme { isNightMode ->
                onSurface.copy(alpha = if (isNightMode) 0.72f else 0.82f)
            }

            return remember(
                primary,
                lumaCurveColor,
                gridLineColor,
                referenceLineColor
            ) {
                ImageCurvesEditorColors(
                    lumaCurveColor = lumaCurveColor.blend(primary, 0.12f),
                    redCurveColor = Color(-0x12c2b4).blend(primary),
                    greenCurveColor = Color(-0xef1163).blend(primary),
                    blueCurveColor = Color(-0xcc8805).blend(primary),
                    guidelinesColor = gridLineColor.copy(alpha = 1f),
                    defaultCurveColor = lumaCurveColor.blend(primary, 0.12f),
                    editorBackgroundColor = Color.Black.copy(alpha = 0.18f),
                    cyanCurveColor = Color(0xFF00BCD4).blend(primary),
                    magentaCurveColor = Color(0xFFEC407A).blend(primary),
                    yellowCurveColor = Color(0xFFFFC107).blend(primary),
                    hueCurveColors = listOf(
                        Color(0xFFFF5252),
                        Color(0xFFFFAB40),
                        Color(0xFFFFE033),
                        Color(0xFF4CD964),
                        Color(0xFF38D6D2),
                        Color(0xFF5596FF),
                        Color(0xFFA66BFF),
                        Color(0xFFF55CAA),
                        Color(0xFFFF5252)
                    ).map { it.blend(primary, 0.12f) },
                    lumaGradientStartColor = Color(0xFF444444).blend(primary),
                    lumaGradientEndColor = Color.White.blend(primary),
                    saturationGradientStartColor = Color.White.blend(primary),
                    saturationGradientEndColor = Color(0xFFFFE632).blend(primary),
                    gridLineAlpha = gridLineColor.alpha,
                    referenceLineAlpha = referenceLineColor.alpha
                )
            }
        }


    private fun Color.blend(
        color: Color,
        @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.25f
    ): Color = Color(ColorUtils.blendARGB(this.toArgb(), color.toArgb(), fraction))
}
