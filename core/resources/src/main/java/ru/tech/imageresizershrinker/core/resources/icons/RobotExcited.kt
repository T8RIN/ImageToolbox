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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.RobotExcited: ImageVector by lazy {
    ImageVector.Builder(
        name = "Robot Excited", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
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
            moveTo(22.0f, 14.0f)
            horizontalLineTo(21.0f)
            curveTo(21.0f, 10.13f, 17.87f, 7.0f, 14.0f, 7.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(5.73f)
            curveTo(13.6f, 5.39f, 14.0f, 4.74f, 14.0f, 4.0f)
            curveTo(14.0f, 2.9f, 13.11f, 2.0f, 12.0f, 2.0f)
            reflectiveCurveTo(10.0f, 2.9f, 10.0f, 4.0f)
            curveTo(10.0f, 4.74f, 10.4f, 5.39f, 11.0f, 5.73f)
            verticalLineTo(7.0f)
            horizontalLineTo(10.0f)
            curveTo(6.13f, 7.0f, 3.0f, 10.13f, 3.0f, 14.0f)
            horizontalLineTo(2.0f)
            curveTo(1.45f, 14.0f, 1.0f, 14.45f, 1.0f, 15.0f)
            verticalLineTo(18.0f)
            curveTo(1.0f, 18.55f, 1.45f, 19.0f, 2.0f, 19.0f)
            horizontalLineTo(3.0f)
            verticalLineTo(20.0f)
            curveTo(3.0f, 21.11f, 3.9f, 22.0f, 5.0f, 22.0f)
            horizontalLineTo(19.0f)
            curveTo(20.11f, 22.0f, 21.0f, 21.11f, 21.0f, 20.0f)
            verticalLineTo(19.0f)
            horizontalLineTo(22.0f)
            curveTo(22.55f, 19.0f, 23.0f, 18.55f, 23.0f, 18.0f)
            verticalLineTo(15.0f)
            curveTo(23.0f, 14.45f, 22.55f, 14.0f, 22.0f, 14.0f)
            moveTo(21.0f, 17.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(17.0f)
            horizontalLineTo(3.0f)
            verticalLineTo(16.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(14.0f)
            curveTo(5.0f, 11.24f, 7.24f, 9.0f, 10.0f, 9.0f)
            horizontalLineTo(14.0f)
            curveTo(16.76f, 9.0f, 19.0f, 11.24f, 19.0f, 14.0f)
            verticalLineTo(16.0f)
            horizontalLineTo(21.0f)
            verticalLineTo(17.0f)
            moveTo(8.5f, 13.5f)
            lineTo(10.86f, 15.86f)
            lineTo(9.68f, 17.04f)
            lineTo(8.5f, 15.86f)
            lineTo(7.32f, 17.04f)
            lineTo(6.14f, 15.86f)
            lineTo(8.5f, 13.5f)
            moveTo(15.5f, 13.5f)
            lineTo(17.86f, 15.86f)
            lineTo(16.68f, 17.04f)
            lineTo(15.5f, 15.86f)
            lineTo(14.32f, 17.04f)
            lineTo(13.14f, 15.86f)
            lineTo(15.5f, 13.5f)
            close()
        }
    }
        .build()
}