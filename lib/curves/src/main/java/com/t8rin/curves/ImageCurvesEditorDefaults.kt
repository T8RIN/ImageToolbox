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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

object ImageCurvesEditorDefaults {

    val Colors: ImageCurvesEditorColors
        @Composable
        get() {
            return ImageCurvesEditorColors(
                lumaCurveColor = Color.White.blend(MaterialTheme.colorScheme.primary),
                redCurveColor = Color(-0x12c2b4).blend(MaterialTheme.colorScheme.primary),
                greenCurveColor = Color(-0xef1163).blend(MaterialTheme.colorScheme.primary),
                blueCurveColor = Color(-0xcc8805).blend(MaterialTheme.colorScheme.primary),
                guidelinesColor = Color(-0x66000001).blend(MaterialTheme.colorScheme.primary),
                defaultCurveColor = Color(-0x66000001).blend(MaterialTheme.colorScheme.primary)
            )
        }


    private fun Color.blend(
        color: Color,
        @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.25f
    ): Color = Color(ColorUtils.blendARGB(this.toArgb(), color.toArgb(), fraction))
}