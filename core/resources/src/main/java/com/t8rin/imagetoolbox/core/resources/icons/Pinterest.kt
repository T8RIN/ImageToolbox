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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Rounded.Pinterest: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Pinterest",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12.017f, 0f)
            curveTo(5.396f, 0f, 0.029f, 5.367f, 0.029f, 11.987f)
            curveToRelative(0f, 5.079f, 3.158f, 9.417f, 7.618f, 11.162f)
            curveToRelative(-0.105f, -0.949f, -0.199f, -2.403f, 0.041f, -3.439f)
            curveToRelative(0.219f, -0.937f, 1.406f, -5.957f, 1.406f, -5.957f)
            reflectiveCurveToRelative(-0.359f, -0.72f, -0.359f, -1.781f)
            curveToRelative(0f, -1.663f, 0.967f, -2.911f, 2.168f, -2.911f)
            curveToRelative(1.024f, 0f, 1.518f, 0.769f, 1.518f, 1.688f)
            curveToRelative(0f, 1.029f, -0.653f, 2.567f, -0.992f, 3.992f)
            curveToRelative(-0.285f, 1.193f, 0.6f, 2.165f, 1.775f, 2.165f)
            curveToRelative(2.128f, 0f, 3.768f, -2.245f, 3.768f, -5.487f)
            curveToRelative(0f, -2.861f, -2.063f, -4.869f, -5.008f, -4.869f)
            curveToRelative(-3.41f, 0f, -5.409f, 2.562f, -5.409f, 5.199f)
            curveToRelative(0f, 1.033f, 0.394f, 2.143f, 0.889f, 2.741f)
            curveToRelative(0.099f, 0.12f, 0.112f, 0.225f, 0.085f, 0.345f)
            curveToRelative(-0.09f, 0.375f, -0.293f, 1.199f, -0.334f, 1.363f)
            curveToRelative(-0.053f, 0.225f, -0.172f, 0.271f, -0.401f, 0.165f)
            curveToRelative(-1.495f, -0.69f, -2.433f, -2.878f, -2.433f, -4.646f)
            curveToRelative(0f, -3.776f, 2.748f, -7.252f, 7.92f, -7.252f)
            curveToRelative(4.158f, 0f, 7.392f, 2.967f, 7.392f, 6.923f)
            curveToRelative(0f, 4.135f, -2.607f, 7.462f, -6.233f, 7.462f)
            curveToRelative(-1.214f, 0f, -2.354f, -0.629f, -2.758f, -1.379f)
            lineToRelative(-0.749f, 2.848f)
            curveToRelative(-0.269f, 1.045f, -1.004f, 2.352f, -1.498f, 3.146f)
            curveToRelative(1.123f, 0.345f, 2.306f, 0.535f, 3.55f, 0.535f)
            curveToRelative(6.607f, 0f, 11.985f, -5.365f, 11.985f, -11.987f)
            curveTo(23.97f, 5.39f, 18.592f, 0.026f, 11.985f, 0.026f)
            lineTo(12.017f, 0f)
            close()
        }
    }.build()
}
