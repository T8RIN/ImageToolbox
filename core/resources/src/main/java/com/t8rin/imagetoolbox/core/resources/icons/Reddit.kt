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

val Icons.Rounded.Reddit: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Reddit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 0f)
            curveTo(5.373f, 0f, 0f, 5.373f, 0f, 12f)
            curveToRelative(0f, 3.314f, 1.343f, 6.314f, 3.515f, 8.485f)
            lineToRelative(-2.286f, 2.286f)
            curveTo(0.775f, 23.225f, 1.097f, 24f, 1.738f, 24f)
            horizontalLineTo(12f)
            curveToRelative(6.627f, 0f, 12f, -5.373f, 12f, -12f)
            reflectiveCurveTo(18.627f, 0f, 12f, 0f)
            close()
            moveToRelative(4.388f, 3.199f)
            curveToRelative(1.104f, 0f, 1.999f, 0.895f, 1.999f, 1.999f)
            curveToRelative(0f, 1.105f, -0.895f, 2f, -1.999f, 2f)
            curveToRelative(-0.946f, 0f, -1.739f, -0.657f, -1.947f, -1.539f)
            verticalLineToRelative(0.002f)
            curveToRelative(-1.147f, 0.162f, -2.032f, 1.15f, -2.032f, 2.341f)
            verticalLineToRelative(0.007f)
            curveToRelative(1.776f, 0.067f, 3.4f, 0.567f, 4.686f, 1.363f)
            curveToRelative(0.473f, -0.363f, 1.064f, -0.58f, 1.707f, -0.58f)
            curveToRelative(1.547f, 0f, 2.802f, 1.254f, 2.802f, 2.802f)
            curveToRelative(0f, 1.117f, -0.655f, 2.081f, -1.601f, 2.531f)
            curveToRelative(-0.088f, 3.256f, -3.637f, 5.876f, -7.997f, 5.876f)
            curveToRelative(-4.361f, 0f, -7.905f, -2.617f, -7.998f, -5.87f)
            curveToRelative(-0.954f, -0.447f, -1.614f, -1.415f, -1.614f, -2.538f)
            curveToRelative(0f, -1.548f, 1.255f, -2.802f, 2.803f, -2.802f)
            curveToRelative(0.645f, 0f, 1.239f, 0.218f, 1.712f, 0.585f)
            curveToRelative(1.275f, -0.79f, 2.881f, -1.291f, 4.64f, -1.365f)
            verticalLineToRelative(-0.01f)
            curveToRelative(0f, -1.663f, 1.263f, -3.034f, 2.88f, -3.207f)
            curveToRelative(0.188f, -0.911f, 0.993f, -1.595f, 1.959f, -1.595f)
            close()
            moveToRelative(-8.085f, 8.376f)
            curveToRelative(-0.784f, 0f, -1.459f, 0.78f, -1.506f, 1.797f)
            curveToRelative(-0.047f, 1.016f, 0.64f, 1.429f, 1.426f, 1.429f)
            curveToRelative(0.786f, 0f, 1.371f, -0.369f, 1.418f, -1.385f)
            curveToRelative(0.047f, -1.017f, -0.553f, -1.841f, -1.338f, -1.841f)
            close()
            moveToRelative(7.406f, 0f)
            curveToRelative(-0.786f, 0f, -1.385f, 0.824f, -1.338f, 1.841f)
            curveToRelative(0.047f, 1.017f, 0.634f, 1.385f, 1.418f, 1.385f)
            curveToRelative(0.785f, 0f, 1.473f, -0.413f, 1.426f, -1.429f)
            curveToRelative(-0.046f, -1.017f, -0.721f, -1.797f, -1.506f, -1.797f)
            close()
            moveToRelative(-3.703f, 4.013f)
            curveToRelative(-0.974f, 0f, -1.907f, 0.048f, -2.77f, 0.135f)
            curveToRelative(-0.147f, 0.015f, -0.241f, 0.168f, -0.183f, 0.305f)
            curveToRelative(0.483f, 1.154f, 1.622f, 1.964f, 2.953f, 1.964f)
            curveToRelative(1.33f, 0f, 2.47f, -0.81f, 2.953f, -1.964f)
            curveToRelative(0.057f, -0.137f, -0.037f, -0.29f, -0.184f, -0.305f)
            curveToRelative(-0.863f, -0.087f, -1.795f, -0.135f, -2.769f, -0.135f)
            close()
        }
    }.build()
}

