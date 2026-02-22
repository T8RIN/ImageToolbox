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

val Icons.Outlined.Counter: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Counter",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 4f)
            horizontalLineTo(20f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 6f)
            verticalLineTo(18f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 20f)
            horizontalLineTo(4f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 18f)
            verticalLineTo(6f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 4f)
            moveTo(4f, 6f)
            verticalLineTo(18f)
            horizontalLineTo(11f)
            verticalLineTo(6f)
            horizontalLineTo(4f)
            moveTo(20f, 18f)
            verticalLineTo(6f)
            horizontalLineTo(18.76f)
            curveTo(19f, 6.54f, 18.95f, 7.07f, 18.95f, 7.13f)
            curveTo(18.88f, 7.8f, 18.41f, 8.5f, 18.24f, 8.75f)
            lineTo(15.91f, 11.3f)
            lineTo(19.23f, 11.28f)
            lineTo(19.24f, 12.5f)
            lineTo(14.04f, 12.47f)
            lineTo(14f, 11.47f)
            curveTo(14f, 11.47f, 17.05f, 8.24f, 17.2f, 7.95f)
            curveTo(17.34f, 7.67f, 17.91f, 6f, 16.5f, 6f)
            curveTo(15.27f, 6.05f, 15.41f, 7.3f, 15.41f, 7.3f)
            lineTo(13.87f, 7.31f)
            curveTo(13.87f, 7.31f, 13.88f, 6.65f, 14.25f, 6f)
            horizontalLineTo(13f)
            verticalLineTo(18f)
            horizontalLineTo(15.58f)
            lineTo(15.57f, 17.14f)
            lineTo(16.54f, 17.13f)
            curveTo(16.54f, 17.13f, 17.45f, 16.97f, 17.46f, 16.08f)
            curveTo(17.5f, 15.08f, 16.65f, 15.08f, 16.5f, 15.08f)
            curveTo(16.37f, 15.08f, 15.43f, 15.13f, 15.43f, 15.95f)
            horizontalLineTo(13.91f)
            curveTo(13.91f, 15.95f, 13.95f, 13.89f, 16.5f, 13.89f)
            curveTo(19.1f, 13.89f, 18.96f, 15.91f, 18.96f, 15.91f)
            curveTo(18.96f, 15.91f, 19f, 17.16f, 17.85f, 17.63f)
            lineTo(18.37f, 18f)
            horizontalLineTo(20f)
            moveTo(8.92f, 16f)
            horizontalLineTo(7.42f)
            verticalLineTo(10.2f)
            lineTo(5.62f, 10.76f)
            verticalLineTo(9.53f)
            lineTo(8.76f, 8.41f)
            horizontalLineTo(8.92f)
            verticalLineTo(16f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Counter: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.Counter",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20f, 4f)
            horizontalLineTo(4f)
            curveToRelative(-1.105f, 0f, -2f, 0.895f, -2f, 2f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 1.105f, 0.895f, 2f, 2f, 2f)
            horizontalLineToRelative(16f)
            curveToRelative(1.105f, 0f, 2f, -0.895f, 2f, -2f)
            verticalLineTo(6f)
            curveToRelative(0f, -1.105f, -0.895f, -2f, -2f, -2f)
            close()
            moveTo(11f, 18f)
            horizontalLineToRelative(-7f)
            verticalLineTo(6f)
            horizontalLineToRelative(7f)
            verticalLineToRelative(12f)
            close()
            moveTo(20f, 18f)
            horizontalLineToRelative(-1.63f)
            lineToRelative(-0.52f, -0.37f)
            curveToRelative(1.15f, -0.47f, 1.11f, -1.72f, 1.11f, -1.72f)
            curveToRelative(0f, 0f, 0.14f, -2.02f, -2.46f, -2.02f)
            curveToRelative(-2.55f, 0f, -2.59f, 2.06f, -2.59f, 2.06f)
            horizontalLineToRelative(1.52f)
            curveToRelative(0f, -0.82f, 0.94f, -0.87f, 1.07f, -0.87f)
            curveToRelative(0.15f, 0f, 1f, 0f, 0.96f, 1f)
            curveToRelative(-0.01f, 0.89f, -0.92f, 1.05f, -0.92f, 1.05f)
            lineToRelative(-0.97f, 0.01f)
            lineToRelative(0.01f, 0.86f)
            horizontalLineToRelative(-2.58f)
            verticalLineTo(6f)
            horizontalLineToRelative(1.25f)
            curveToRelative(-0.37f, 0.65f, -0.38f, 1.31f, -0.38f, 1.31f)
            lineToRelative(1.54f, -0.01f)
            reflectiveCurveToRelative(-0.14f, -1.25f, 1.09f, -1.3f)
            curveToRelative(1.41f, 0f, 0.84f, 1.67f, 0.7f, 1.95f)
            curveToRelative(-0.15f, 0.29f, -3.2f, 3.52f, -3.2f, 3.52f)
            lineToRelative(0.04f, 1f)
            lineToRelative(5.2f, 0.03f)
            lineToRelative(-0.01f, -1.22f)
            lineToRelative(-3.32f, 0.02f)
            lineToRelative(2.33f, -2.55f)
            curveToRelative(0.17f, -0.25f, 0.64f, -0.95f, 0.71f, -1.62f)
            curveToRelative(0f, -0.06f, 0.05f, -0.59f, -0.19f, -1.13f)
            horizontalLineToRelative(1.24f)
            verticalLineToRelative(12f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(8.92f, 16f)
            lineToRelative(-1.5f, 0f)
            lineToRelative(0f, -5.8f)
            lineToRelative(-1.8f, 0.56f)
            lineToRelative(0f, -1.23f)
            lineToRelative(3.14f, -1.12f)
            lineToRelative(0.16f, 0f)
            lineToRelative(0f, 7.59f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(4f, 6f)
            lineToRelative(0f, 12f)
            lineToRelative(7f, 0f)
            lineToRelative(0f, -12f)
            lineToRelative(-7f, 0f)
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(20f, 18f)
            verticalLineTo(6f)
            horizontalLineToRelative(-1.24f)
            curveToRelative(0.24f, 0.54f, 0.19f, 1.07f, 0.19f, 1.13f)
            curveToRelative(-0.07f, 0.67f, -0.54f, 1.37f, -0.71f, 1.62f)
            lineToRelative(-2.33f, 2.55f)
            lineToRelative(3.32f, -0.02f)
            lineToRelative(0.01f, 1.22f)
            lineToRelative(-5.2f, -0.03f)
            lineToRelative(-0.04f, -1f)
            reflectiveCurveToRelative(3.05f, -3.23f, 3.2f, -3.52f)
            curveToRelative(0.14f, -0.28f, 0.71f, -1.95f, -0.7f, -1.95f)
            curveToRelative(-1.23f, 0.05f, -1.09f, 1.3f, -1.09f, 1.3f)
            lineToRelative(-1.54f, 0.01f)
            reflectiveCurveToRelative(0.01f, -0.66f, 0.38f, -1.31f)
            horizontalLineToRelative(-1.25f)
            verticalLineToRelative(12f)
            horizontalLineToRelative(2.58f)
            lineToRelative(-0.01f, -0.86f)
            lineToRelative(0.97f, -0.01f)
            reflectiveCurveToRelative(0.91f, -0.16f, 0.92f, -1.05f)
            curveToRelative(0.04f, -1f, -0.81f, -1f, -0.96f, -1f)
            curveToRelative(-0.13f, 0f, -1.07f, 0.05f, -1.07f, 0.87f)
            horizontalLineToRelative(-1.52f)
            reflectiveCurveToRelative(0.04f, -2.06f, 2.59f, -2.06f)
            curveToRelative(2.6f, 0f, 2.46f, 2.02f, 2.46f, 2.02f)
            curveToRelative(0f, 0f, 0.04f, 1.25f, -1.11f, 1.72f)
            lineToRelative(0.52f, 0.37f)
            horizontalLineToRelative(1.63f)
        }
    }.build()
}