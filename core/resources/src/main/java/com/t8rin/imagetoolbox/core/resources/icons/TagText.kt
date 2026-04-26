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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.TagText: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.TagText",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.4f, 11.6f)
            lineTo(12.4f, 2.6f)
            curveToRelative(-0.4f, -0.4f, -0.9f, -0.6f, -1.4f, -0.6f)
            horizontalLineToRelative(-7f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(7f)
            curveToRelative(0f, 0.5f, 0.2f, 1f, 0.6f, 1.4f)
            lineToRelative(9f, 9f)
            curveToRelative(0.4f, 0.4f, 0.9f, 0.6f, 1.4f, 0.6f)
            reflectiveCurveToRelative(1f, -0.2f, 1.4f, -0.6f)
            lineToRelative(7f, -7f)
            curveToRelative(0.4f, -0.4f, 0.6f, -0.9f, 0.6f, -1.4f)
            reflectiveCurveToRelative(-0.2f, -1f, -0.6f, -1.4f)
            close()
            moveTo(13f, 20f)
            lineTo(4f, 11f)
            verticalLineToRelative(-7f)
            horizontalLineToRelative(7f)
            lineToRelative(9f, 9f)
            lineToRelative(-7f, 7f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(6.5f, 5f)
            curveToRelative(0.8f, 0f, 1.5f, 0.7f, 1.5f, 1.5f)
            reflectiveCurveToRelative(-0.7f, 1.5f, -1.5f, 1.5f)
            reflectiveCurveToRelative(-1.5f, -0.7f, -1.5f, -1.5f)
            reflectiveCurveToRelative(0.7f, -1.5f, 1.5f, -1.5f)
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(8.3f, 10.7f)
            lineTo(8.3f, 10.7f)
            arcTo(0.99f, 0.99f, 90f, isMoreThanHalf = false, isPositiveArc = true, 9.7f, 10.7f)
            lineTo(12.3f, 13.3f)
            arcTo(0.99f, 0.99f, 90f, isMoreThanHalf = false, isPositiveArc = true, 12.3f, 14.7f)
            lineTo(12.3f, 14.7f)
            arcTo(0.99f, 0.99f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.9f, 14.7f)
            lineTo(8.3f, 12.1f)
            arcTo(0.99f, 0.99f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.3f, 10.7f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.609f, 13.988f)
            curveToRelative(0.25f, -0.002f, 0.5f, -0.097f, 0.691f, -0.288f)
            curveToRelative(0.387f, -0.387f, 0.387f, -1.013f, 0f, -1.4f)
            lineToRelative(-4.1f, -4.1f)
            curveToRelative(-0.191f, -0.191f, -0.441f, -0.286f, -0.691f, -0.288f)
            curveToRelative(-0.25f, 0.002f, -0.5f, 0.097f, -0.691f, 0.288f)
            curveToRelative(-0.387f, 0.387f, -0.387f, 1.013f, 0f, 1.4f)
            lineToRelative(4.1f, 4.1f)
            curveToRelative(0.191f, 0.191f, 0.441f, 0.286f, 0.691f, 0.288f)
            close()
        }
    }.build()
}

val Icons.TwoTone.TagText: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.TagText",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(13f, 20f)
            lineToRelative(-9f, -9f)
            lineToRelative(0f, -7f)
            lineToRelative(7f, 0f)
            lineToRelative(9f, 9f)
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.4f, 11.6f)
            lineTo(12.4f, 2.6f)
            curveToRelative(-0.4f, -0.4f, -0.9f, -0.6f, -1.4f, -0.6f)
            horizontalLineToRelative(-7f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(7f)
            curveToRelative(0f, 0.5f, 0.2f, 1f, 0.6f, 1.4f)
            lineToRelative(9f, 9f)
            curveToRelative(0.4f, 0.4f, 0.9f, 0.6f, 1.4f, 0.6f)
            reflectiveCurveToRelative(1f, -0.2f, 1.4f, -0.6f)
            lineToRelative(7f, -7f)
            curveToRelative(0.4f, -0.4f, 0.6f, -0.9f, 0.6f, -1.4f)
            reflectiveCurveToRelative(-0.2f, -1f, -0.6f, -1.4f)
            close()
            moveTo(13f, 20f)
            lineTo(4f, 11f)
            verticalLineToRelative(-7f)
            horizontalLineToRelative(7f)
            lineToRelative(9f, 9f)
            lineToRelative(-7f, 7f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(6.5f, 5f)
            curveToRelative(0.8f, 0f, 1.5f, 0.7f, 1.5f, 1.5f)
            reflectiveCurveToRelative(-0.7f, 1.5f, -1.5f, 1.5f)
            reflectiveCurveToRelative(-1.5f, -0.7f, -1.5f, -1.5f)
            reflectiveCurveToRelative(0.7f, -1.5f, 1.5f, -1.5f)
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(8.3f, 10.7f)
            lineTo(8.3f, 10.7f)
            arcTo(0.99f, 0.99f, 90f, isMoreThanHalf = false, isPositiveArc = true, 9.7f, 10.7f)
            lineTo(12.3f, 13.3f)
            arcTo(0.99f, 0.99f, 90f, isMoreThanHalf = false, isPositiveArc = true, 12.3f, 14.7f)
            lineTo(12.3f, 14.7f)
            arcTo(0.99f, 0.99f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.9f, 14.7f)
            lineTo(8.3f, 12.1f)
            arcTo(0.99f, 0.99f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.3f, 10.7f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.609f, 13.988f)
            curveToRelative(0.25f, -0.002f, 0.5f, -0.097f, 0.691f, -0.288f)
            curveToRelative(0.387f, -0.387f, 0.387f, -1.013f, 0f, -1.4f)
            lineToRelative(-4.1f, -4.1f)
            curveToRelative(-0.191f, -0.191f, -0.441f, -0.286f, -0.691f, -0.288f)
            curveToRelative(-0.25f, 0.002f, -0.5f, 0.097f, -0.691f, 0.288f)
            curveToRelative(-0.387f, 0.387f, -0.387f, 1.013f, 0f, 1.4f)
            lineToRelative(4.1f, 4.1f)
            curveToRelative(0.191f, 0.191f, 0.441f, 0.286f, 0.691f, 0.288f)
            close()
        }
    }.build()
}