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
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Analytics: ImageVector by lazy {
    Builder(
        name = "Analytics", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 192.0f, viewportHeight = 192.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFFF9AB00)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(130.0f, 29.0f)
            verticalLineToRelative(132.0f)
            curveToRelative(0.0f, 14.77f, 10.19f, 23.0f, 21.0f, 23.0f)
            curveToRelative(10.0f, 0.0f, 21.0f, -7.0f, 21.0f, -23.0f)
            verticalLineTo(30.0f)
            curveToRelative(0.0f, -13.54f, -10.0f, -22.0f, -21.0f, -22.0f)
            reflectiveCurveTo(130.0f, 17.33f, 130.0f, 29.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFE37400)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(75.0f, 96.0f)
            verticalLineToRelative(65.0f)
            curveToRelative(0.0f, 14.77f, 10.19f, 23.0f, 21.0f, 23.0f)
            curveToRelative(10.0f, 0.0f, 21.0f, -7.0f, 21.0f, -23.0f)
            verticalLineTo(97.0f)
            curveToRelative(0.0f, -13.54f, -10.0f, -22.0f, -21.0f, -22.0f)
            reflectiveCurveTo(75.0f, 84.33f, 75.0f, 96.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFE37400)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(41.0f, 163.0f)
            moveToRelative(-21.0f, 0.0f)
            arcToRelative(
                21.0f, 21.0f, 0.0f,
                isMoreThanHalf = true,
                isPositiveArc = true,
                dx1 = 42.0f,
                dy1 = 0.0f
            )
            arcToRelative(
                21.0f, 21.0f, 0.0f,
                isMoreThanHalf = true,
                isPositiveArc = true,
                dx1 = -42.0f,
                dy1 = 0.0f
            )
        }
    }
        .build()
}