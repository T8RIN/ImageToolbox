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

package ru.tech.imageresizershrinker.core.ui.icons.material

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.CreateAlt: ImageVector by lazy {
    ImageVector.Builder(
        name = "CreateAlt", defaultWidth = 18.2.dp, defaultHeight = 18.2.dp,
        viewportWidth = 18.2f, viewportHeight = 18.2f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(2.0f, 16.2f)
            horizontalLineToRelative(1.4f)
            lineToRelative(10.0f, -10.0f)
            lineToRelative(-0.7f, -0.7f)
            lineToRelative(-0.8f, -0.7f)
            lineTo(2.0f, 14.9f)
            verticalLineTo(16.2f)
            close()
            moveTo(0.0f, 18.2f)
            verticalLineTo(14.0f)
            lineTo(13.4f, 0.6f)
            curveTo(13.7f, 0.2f, 14.2f, 0.0f, 14.8f, 0.0f)
            curveToRelative(0.6f, 0.0f, 1.0f, 0.2f, 1.4f, 0.6f)
            lineTo(17.6f, 2.0f)
            curveToRelative(0.4f, 0.4f, 0.6f, 0.8f, 0.6f, 1.4f)
            reflectiveCurveToRelative(-0.2f, 1.0f, -0.6f, 1.4f)
            lineTo(4.2f, 18.2f)
            horizontalLineTo(0.0f)
            close()
            moveTo(16.1f, 3.4f)
            lineTo(14.8f, 2.0f)
            lineTo(16.1f, 3.4f)
            close()
            moveTo(13.4f, 6.2f)
            lineToRelative(-0.7f, -0.7f)
            lineToRelative(-0.8f, -0.7f)
            lineTo(13.4f, 6.2f)
            close()
        }
    }.build()
}