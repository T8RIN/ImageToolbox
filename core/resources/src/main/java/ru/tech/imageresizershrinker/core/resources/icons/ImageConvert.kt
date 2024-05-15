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
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.ImageConvert: ImageVector by lazy {
    ImageVector.Builder(
        name = "ImageConvert", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(8.6f, 19.9f)
            curveToRelative(-1.6f, -0.7f, -2.9f, -1.8f, -3.9f, -3.3f)
            curveToRelative(-1.0f, -1.5f, -1.5f, -3.1f, -1.5f, -5.0f)
            curveToRelative(0.0f, -0.4f, 0.0f, -0.8f, 0.1f, -1.2f)
            curveToRelative(0.0f, -0.4f, 0.1f, -0.8f, 0.2f, -1.1f)
            lineTo(2.4f, 10.0f)
            lineTo(1.5f, 8.4f)
            lineToRelative(4.4f, -2.5f)
            lineToRelative(2.5f, 4.3f)
            lineToRelative(-1.6f, 0.9f)
            lineTo(5.5f, 9.0f)
            curveToRelative(-0.2f, 0.4f, -0.3f, 0.8f, -0.4f, 1.3f)
            curveTo(5.1f, 10.7f, 5.0f, 11.1f, 5.0f, 11.6f)
            curveToRelative(0.0f, 1.5f, 0.4f, 2.8f, 1.2f, 4.0f)
            reflectiveCurveToRelative(1.9f, 2.1f, 3.2f, 2.7f)
            lineTo(8.6f, 19.9f)
            close()
            moveTo(15.6f, 8.9f)
            verticalLineTo(7.0f)
            horizontalLineTo(18.0f)
            curveToRelative(-0.7f, -0.9f, -1.5f, -1.5f, -2.5f, -2.0f)
            reflectiveCurveToRelative(-2.0f, -0.7f, -3.2f, -0.7f)
            curveToRelative(-0.8f, 0.0f, -1.6f, 0.1f, -2.4f, 0.4f)
            curveTo(9.2f, 5.0f, 8.5f, 5.3f, 7.9f, 5.8f)
            lineTo(7.0f, 4.2f)
            curveTo(7.8f, 3.7f, 8.6f, 3.3f, 9.5f, 3.0f)
            reflectiveCurveToRelative(1.9f, -0.5f, 2.9f, -0.5f)
            curveToRelative(1.2f, 0.0f, 2.4f, 0.2f, 3.5f, 0.7f)
            reflectiveCurveToRelative(2.1f, 1.1f, 2.9f, 1.9f)
            verticalLineTo(3.9f)
            horizontalLineToRelative(1.8f)
            verticalLineToRelative(5.0f)
            horizontalLineTo(15.6f)
            close()
            moveTo(15.0f, 22.5f)
            lineTo(10.6f, 20.0f)
            lineToRelative(2.5f, -4.3f)
            lineToRelative(1.6f, 0.9f)
            lineToRelative(-1.3f, 2.2f)
            curveToRelative(1.8f, -0.3f, 3.3f, -1.1f, 4.5f, -2.4f)
            curveToRelative(1.2f, -1.4f, 1.8f, -3.0f, 1.8f, -4.8f)
            curveToRelative(0.0f, -0.2f, 0.0f, -0.3f, 0.0f, -0.5f)
            reflectiveCurveToRelative(0.0f, -0.3f, -0.1f, -0.4f)
            horizontalLineToRelative(1.9f)
            curveToRelative(0.0f, 0.2f, 0.0f, 0.3f, 0.0f, 0.4f)
            reflectiveCurveToRelative(0.0f, 0.3f, 0.0f, 0.5f)
            curveToRelative(0.0f, 2.0f, -0.6f, 3.9f, -1.8f, 5.5f)
            reflectiveCurveToRelative(-2.8f, 2.7f, -4.8f, 3.3f)
            lineToRelative(1.0f, 0.6f)
            lineTo(15.0f, 22.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(8.2f, 13.5f)
            lineToRelative(8.0f, 0.0f)
            lineToRelative(-2.6f, -3.5f)
            lineToRelative(-1.9f, 2.5f)
            lineToRelative(-1.3f, -1.8f)
            close()
        }
    }.build()
}