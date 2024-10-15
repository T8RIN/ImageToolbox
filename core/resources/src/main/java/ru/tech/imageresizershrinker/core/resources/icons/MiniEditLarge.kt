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

package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.MiniEditLarge: ImageVector by lazy {
    ImageVector.Builder(
        name = "MiniEditLarge",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(20.775f, 6.75f)
            curveToRelative(-0.15f, -0.367f, -0.358f, -0.7f, -0.625f, -1f)
            lineToRelative(-1.85f, -1.85f)
            curveTo(18f, 3.6f, 17.667f, 3.375f, 17.3f, 3.225f)
            curveTo(16.933f, 3.075f, 16.55f, 3f, 16.15f, 3f)
            curveToRelative(-0.183f, 0f, -0.367f, 0.017f, -0.55f, 0.05f)
            curveToRelative(-0.183f, 0.033f, -0.367f, 0.083f, -0.55f, 0.15f)
            curveToRelative(-0.367f, 0.133f, -0.7f, 0.35f, -1f, 0.65f)
            lineToRelative(-0.461f, 0.458f)
            lineTo(3.6f, 14.25f)
            curveTo(3.4f, 14.45f, 3.25f, 14.675f, 3.15f, 14.925f)
            reflectiveCurveTo(3f, 15.433f, 3f, 15.7f)
            verticalLineTo(19f)
            curveToRelative(0f, 0.567f, 0.192f, 1.042f, 0.575f, 1.425f)
            curveTo(3.958f, 20.808f, 4.433f, 21f, 5f, 21f)
            horizontalLineToRelative(3.3f)
            curveToRelative(0.267f, 0f, 0.525f, -0.05f, 0.775f, -0.15f)
            reflectiveCurveToRelative(0.475f, -0.25f, 0.675f, -0.45f)
            lineToRelative(2.002f, -2.002f)
            lineTo(20.15f, 10f)
            curveToRelative(0.15f, -0.15f, 0.279f, -0.31f, 0.387f, -0.481f)
            reflectiveCurveToRelative(0.196f, -0.352f, 0.263f, -0.544f)
            curveTo(20.867f, 8.783f, 20.917f, 8.594f, 20.95f, 8.406f)
            reflectiveCurveTo(21f, 8.033f, 21f, 7.85f)
            curveTo(21f, 7.483f, 20.925f, 7.117f, 20.775f, 6.75f)
            close()
            moveTo(16.15f, 6f)
            lineTo(18f, 7.85f)
            lineToRelative(-1.85f, 1.95f)
            lineTo(14.25f, 7.9f)
            lineTo(16.15f, 6f)
            close()
        }
    }.build()
}


val Icons.Outlined.MiniEditLarge: ImageVector by lazy {
    ImageVector.Builder(
        name = "MiniEditLarge Outlined",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(20.775f, 6.75f)
            curveToRelative(-0.15f, -0.367f, -0.358f, -0.7f, -0.625f, -1f)
            lineToRelative(-1.85f, -1.85f)
            curveTo(18f, 3.6f, 17.667f, 3.375f, 17.3f, 3.225f)
            curveTo(16.933f, 3.075f, 16.55f, 3f, 16.15f, 3f)
            curveToRelative(-0.367f, 0f, -0.733f, 0.067f, -1.1f, 0.2f)
            curveToRelative(-0.367f, 0.133f, -0.7f, 0.35f, -1f, 0.65f)
            lineTo(3.6f, 14.25f)
            curveTo(3.4f, 14.45f, 3.25f, 14.675f, 3.15f, 14.925f)
            reflectiveCurveTo(3f, 15.433f, 3f, 15.7f)
            verticalLineTo(19f)
            curveToRelative(0f, 0.567f, 0.192f, 1.042f, 0.575f, 1.425f)
            curveTo(3.958f, 20.808f, 4.433f, 21f, 5f, 21f)
            horizontalLineToRelative(3.3f)
            curveToRelative(0.267f, 0f, 0.525f, -0.05f, 0.775f, -0.15f)
            reflectiveCurveToRelative(0.475f, -0.25f, 0.675f, -0.45f)
            lineTo(20.15f, 10f)
            curveToRelative(0.3f, -0.3f, 0.517f, -0.642f, 0.65f, -1.025f)
            curveTo(20.933f, 8.592f, 21f, 8.217f, 21f, 7.85f)
            reflectiveCurveTo(20.925f, 7.117f, 20.775f, 6.75f)
            close()
            moveTo(7.9f, 18f)
            horizontalLineTo(6f)
            verticalLineToRelative(-1.9f)
            lineToRelative(6.1f, -6.05f)
            lineToRelative(0.95f, 0.9f)
            lineToRelative(0.9f, 0.95f)
            lineTo(7.9f, 18f)
            close()
        }
    }.build()
}