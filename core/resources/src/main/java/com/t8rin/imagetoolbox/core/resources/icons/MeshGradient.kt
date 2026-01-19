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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.MeshGradient: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.MeshGradient",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19f, 3f)
            horizontalLineTo(5f)
            curveToRelative(-1.11f, 0f, -2f, 0.89f, -2f, 2f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.105f, 0.895f, 2f, 2f, 2f)
            horizontalLineToRelative(14f)
            curveToRelative(1.105f, 0f, 2f, -0.895f, 2f, -2f)
            verticalLineTo(5f)
            curveToRelative(0f, -1.11f, -0.9f, -2f, -2f, -2f)
            close()
            moveTo(19f, 13.086f)
            curveToRelative(-1.067f, 0.87f, -2.173f, 1.365f, -3.294f, 1.453f)
            curveToRelative(-0.259f, 0.022f, -0.499f, 0.012f, -0.734f, -0.007f)
            curveToRelative(0.073f, -0.827f, 0.363f, -1.543f, 0.698f, -2.34f)
            curveToRelative(0.2f, -0.476f, 0.41f, -0.983f, 0.576f, -1.535f)
            curveToRelative(0.943f, -0.129f, 1.863f, -0.464f, 2.754f, -0.974f)
            verticalLineToRelative(3.403f)
            close()
            moveTo(5f, 11.005f)
            curveToRelative(1.239f, -1.126f, 2.488f, -1.75f, 3.717f, -1.851f)
            curveToRelative(-0.087f, 0.799f, -0.38f, 1.492f, -0.703f, 2.264f)
            curveToRelative(-0.226f, 0.538f, -0.46f, 1.118f, -0.633f, 1.757f)
            curveToRelative(-0.805f, 0.215f, -1.6f, 0.602f, -2.381f, 1.121f)
            verticalLineToRelative(-3.29f)
            close()
            moveTo(14.54f, 8.294f)
            curveToRelative(0.011f, 0.128f, -0.004f, 0.242f, -0.003f, 0.364f)
            curveToRelative(-0.67f, -0.121f, -1.286f, -0.364f, -1.954f, -0.644f)
            curveToRelative(-0.603f, -0.254f, -1.262f, -0.514f, -1.996f, -0.687f)
            curveToRelative(-0.159f, -0.795f, -0.468f, -1.571f, -0.9f, -2.327f)
            horizontalLineToRelative(3.4f)
            curveToRelative(0.87f, 1.067f, 1.365f, 2.173f, 1.453f, 3.294f)
            close()
            moveTo(14.144f, 10.634f)
            curveToRelative(-0.098f, 0.257f, -0.205f, 0.516f, -0.318f, 0.784f)
            curveToRelative(-0.32f, 0.762f, -0.665f, 1.599f, -0.808f, 2.586f)
            curveToRelative(-0.145f, -0.059f, -0.287f, -0.115f, -0.436f, -0.178f)
            curveToRelative(-0.875f, -0.368f, -1.855f, -0.756f, -3.043f, -0.844f)
            curveToRelative(0.098f, -0.258f, 0.205f, -0.519f, 0.319f, -0.789f)
            curveToRelative(0.34f, -0.807f, 0.71f, -1.696f, 0.838f, -2.762f)
            curveToRelative(0.365f, 0.123f, 0.729f, 0.266f, 1.112f, 0.427f)
            curveToRelative(0.698f, 0.294f, 1.455f, 0.613f, 2.337f, 0.776f)
            close()
            moveTo(9.145f, 15.232f)
            curveToRelative(-0.007f, -0.094f, 0.007f, -0.177f, 0.006f, -0.267f)
            curveToRelative(0.964f, 0.017f, 1.762f, 0.329f, 2.657f, 0.705f)
            curveToRelative(0.398f, 0.168f, 0.817f, 0.342f, 1.266f, 0.493f)
            curveToRelative(0.187f, 0.961f, 0.61f, 1.909f, 1.226f, 2.837f)
            horizontalLineToRelative(-3.294f)
            curveToRelative(-1.142f, -1.256f, -1.772f, -2.523f, -1.86f, -3.768f)
            close()
            moveTo(19f, 7.277f)
            curveToRelative(-0.798f, 0.65f, -1.618f, 1.091f, -2.45f, 1.306f)
            curveToRelative(-0.002f, -0.147f, -0.003f, -0.294f, -0.016f, -0.449f)
            curveToRelative(-0.086f, -1.077f, -0.445f, -2.124f, -1.031f, -3.135f)
            horizontalLineToRelative(3.497f)
            verticalLineToRelative(2.277f)
            close()
            moveTo(7.278f, 5f)
            curveToRelative(0.575f, 0.707f, 0.974f, 1.432f, 1.209f, 2.167f)
            curveToRelative(-1.183f, 0.109f, -2.35f, 0.55f, -3.487f, 1.304f)
            verticalLineToRelative(-3.472f)
            horizontalLineToRelative(2.278f)
            close()
            moveTo(5f, 16.815f)
            curveToRelative(0.716f, -0.65f, 1.435f, -1.116f, 2.152f, -1.427f)
            curveToRelative(0.089f, 1.226f, 0.536f, 2.435f, 1.318f, 3.612f)
            horizontalLineToRelative(-3.47f)
            verticalLineToRelative(-2.185f)
            close()
            moveTo(16.815f, 19f)
            curveToRelative(-0.739f, -0.813f, -1.242f, -1.631f, -1.542f, -2.445f)
            curveToRelative(0.192f, 0.002f, 0.389f, -0.004f, 0.592f, -0.021f)
            curveToRelative(1.077f, -0.086f, 2.124f, -0.445f, 3.135f, -1.031f)
            verticalLineToRelative(3.497f)
            horizontalLineToRelative(-2.185f)
            close()
        }
    }.build()
}