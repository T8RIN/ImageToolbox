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

val Icons.Rounded.Pix: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Pix",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.45f, 16.52f)
            lineToRelative(-3.01f, -3.01f)
            curveToRelative(-0.11f, -0.11f, -0.24f, -0.13f, -0.31f, -0.13f)
            reflectiveCurveToRelative(-0.2f, 0.02f, -0.31f, 0.13f)
            lineTo(8.8f, 16.53f)
            curveToRelative(-0.34f, 0.34f, -0.87f, 0.89f, -2.64f, 0.89f)
            lineToRelative(3.71f, 3.7f)
            curveToRelative(1.17f, 1.17f, 3.07f, 1.17f, 4.24f, 0f)
            lineToRelative(3.72f, -3.71f)
            curveTo(16.92f, 17.41f, 16.16f, 17.23f, 15.45f, 16.52f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(8.8f, 7.47f)
            lineToRelative(3.02f, 3.02f)
            curveToRelative(0.08f, 0.08f, 0.2f, 0.13f, 0.31f, 0.13f)
            reflectiveCurveToRelative(0.23f, -0.05f, 0.31f, -0.13f)
            lineToRelative(2.99f, -2.99f)
            curveToRelative(0.71f, -0.74f, 1.52f, -0.91f, 2.43f, -0.91f)
            lineToRelative(-3.72f, -3.71f)
            curveToRelative(-1.17f, -1.17f, -3.07f, -1.17f, -4.24f, 0f)
            lineToRelative(-3.71f, 3.7f)
            curveTo(7.95f, 6.58f, 8.49f, 7.16f, 8.8f, 7.47f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.11f, 9.85f)
            lineToRelative(-2.25f, -2.26f)
            horizontalLineTo(17.6f)
            curveToRelative(-0.54f, 0f, -1.08f, 0.22f, -1.45f, 0.61f)
            lineToRelative(-3f, 3f)
            curveToRelative(-0.28f, 0.28f, -0.65f, 0.42f, -1.02f, 0.42f)
            curveToRelative(-0.36f, 0f, -0.74f, -0.15f, -1.02f, -0.42f)
            lineTo(8.09f, 8.17f)
            curveToRelative(-0.38f, -0.38f, -0.9f, -0.6f, -1.45f, -0.6f)
            horizontalLineTo(5.17f)
            lineToRelative(-2.29f, 2.3f)
            curveToRelative(-1.17f, 1.17f, -1.17f, 3.07f, 0f, 4.24f)
            lineToRelative(2.29f, 2.3f)
            horizontalLineToRelative(1.48f)
            curveToRelative(0.54f, 0f, 1.06f, -0.22f, 1.45f, -0.6f)
            lineToRelative(3.02f, -3.02f)
            curveToRelative(0.28f, -0.28f, 0.65f, -0.42f, 1.02f, -0.42f)
            curveToRelative(0.37f, 0f, 0.74f, 0.14f, 1.02f, 0.42f)
            lineToRelative(3.01f, 3.01f)
            curveToRelative(0.38f, 0.38f, 0.9f, 0.6f, 1.45f, 0.6f)
            horizontalLineToRelative(1.26f)
            lineToRelative(2.25f, -2.26f)
            curveTo(22.3f, 12.96f, 22.3f, 11.04f, 21.11f, 9.85f)
            close()
        }
    }.build()
}
