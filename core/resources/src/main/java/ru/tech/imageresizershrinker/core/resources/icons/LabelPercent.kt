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

val Icons.Outlined.LabelPercent: ImageVector by lazy {
    Builder(
        name = "Label Percent", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(16.0f, 17.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(16.0f)
            lineTo(19.55f, 12.0f)
            moveTo(17.63f, 5.84f)
            curveTo(17.27f, 5.33f, 16.67f, 5.0f, 16.0f, 5.0f)
            horizontalLineTo(5.0f)
            curveTo(3.9f, 5.0f, 3.0f, 5.9f, 3.0f, 7.0f)
            verticalLineTo(17.0f)
            curveTo(3.0f, 18.11f, 3.9f, 19.0f, 5.0f, 19.0f)
            horizontalLineTo(16.0f)
            curveTo(16.67f, 19.0f, 17.27f, 18.66f, 17.63f, 18.15f)
            lineTo(22.0f, 12.0f)
            lineTo(17.63f, 5.84f)
            moveTo(13.8f, 8.0f)
            lineTo(15.0f, 9.2f)
            lineTo(8.2f, 16.0f)
            lineTo(7.0f, 14.8f)
            moveTo(8.45f, 8.03f)
            curveTo(9.23f, 8.03f, 9.87f, 8.67f, 9.87f, 9.45f)
            reflectiveCurveTo(9.23f, 10.87f, 8.45f, 10.87f)
            reflectiveCurveTo(7.03f, 10.23f, 7.03f, 9.45f)
            reflectiveCurveTo(7.67f, 8.03f, 8.45f, 8.03f)
            moveTo(13.55f, 13.13f)
            curveTo(14.33f, 13.13f, 14.97f, 13.77f, 14.97f, 14.55f)
            curveTo(14.97f, 15.33f, 14.33f, 15.97f, 13.55f, 15.97f)
            curveTo(12.77f, 15.97f, 12.13f, 15.33f, 12.13f, 14.55f)
            curveTo(12.13f, 13.77f, 12.77f, 13.13f, 13.55f, 13.13f)
            close()
        }
    }.build()
}

val Icons.Rounded.LabelPercent: ImageVector by lazy {
    Builder(
        name = "Label Percent", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(17.63f, 5.84f)
            curveTo(17.27f, 5.33f, 16.67f, 5.0f, 16.0f, 5.0f)
            horizontalLineTo(5.0f)
            curveTo(3.9f, 5.0f, 3.0f, 5.9f, 3.0f, 7.0f)
            verticalLineTo(17.0f)
            curveTo(3.0f, 18.11f, 3.9f, 19.0f, 5.0f, 19.0f)
            horizontalLineTo(16.0f)
            curveTo(16.67f, 19.0f, 17.27f, 18.66f, 17.63f, 18.15f)
            lineTo(22.0f, 12.0f)
            lineTo(17.63f, 5.84f)
            moveTo(8.45f, 8.03f)
            curveTo(9.23f, 8.03f, 9.87f, 8.67f, 9.87f, 9.45f)
            reflectiveCurveTo(9.23f, 10.87f, 8.45f, 10.87f)
            reflectiveCurveTo(7.03f, 10.23f, 7.03f, 9.45f)
            reflectiveCurveTo(7.67f, 8.03f, 8.45f, 8.03f)
            moveTo(13.55f, 15.97f)
            curveTo(12.77f, 15.97f, 12.13f, 15.33f, 12.13f, 14.55f)
            reflectiveCurveTo(12.77f, 13.13f, 13.55f, 13.13f)
            reflectiveCurveTo(14.97f, 13.77f, 14.97f, 14.55f)
            reflectiveCurveTo(14.33f, 15.97f, 13.55f, 15.97f)
            moveTo(8.2f, 16.0f)
            lineTo(7.0f, 14.8f)
            lineTo(13.8f, 8.0f)
            lineTo(15.0f, 9.2f)
            lineTo(8.2f, 16.0f)
            close()
        }
    }.build()
}