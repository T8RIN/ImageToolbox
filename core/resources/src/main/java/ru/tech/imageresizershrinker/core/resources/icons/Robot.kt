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

val Icons.Rounded.Robot: ImageVector by lazy {
    Builder(
        name = "Robot", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(20.0f, 4.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(3.0f)
            horizontalLineTo(20.5f)
            curveTo(20.78f, 3.0f, 21.0f, 3.22f, 21.0f, 3.5f)
            verticalLineTo(5.5f)
            curveTo(21.0f, 5.78f, 20.78f, 6.0f, 20.5f, 6.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(5.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(4.0f)
            moveTo(19.0f, 9.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(8.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(9.0f)
            moveTo(17.0f, 3.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(17.0f)
            verticalLineTo(3.0f)
            moveTo(23.0f, 15.0f)
            verticalLineTo(18.0f)
            curveTo(23.0f, 18.55f, 22.55f, 19.0f, 22.0f, 19.0f)
            horizontalLineTo(21.0f)
            verticalLineTo(20.0f)
            curveTo(21.0f, 21.11f, 20.11f, 22.0f, 19.0f, 22.0f)
            horizontalLineTo(5.0f)
            curveTo(3.9f, 22.0f, 3.0f, 21.11f, 3.0f, 20.0f)
            verticalLineTo(19.0f)
            horizontalLineTo(2.0f)
            curveTo(1.45f, 19.0f, 1.0f, 18.55f, 1.0f, 18.0f)
            verticalLineTo(15.0f)
            curveTo(1.0f, 14.45f, 1.45f, 14.0f, 2.0f, 14.0f)
            horizontalLineTo(3.0f)
            curveTo(3.0f, 10.13f, 6.13f, 7.0f, 10.0f, 7.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(5.73f)
            curveTo(10.4f, 5.39f, 10.0f, 4.74f, 10.0f, 4.0f)
            curveTo(10.0f, 2.9f, 10.9f, 2.0f, 12.0f, 2.0f)
            reflectiveCurveTo(14.0f, 2.9f, 14.0f, 4.0f)
            curveTo(14.0f, 4.74f, 13.6f, 5.39f, 13.0f, 5.73f)
            verticalLineTo(7.0f)
            horizontalLineTo(14.0f)
            curveTo(14.34f, 7.0f, 14.67f, 7.03f, 15.0f, 7.08f)
            verticalLineTo(10.0f)
            horizontalLineTo(19.74f)
            curveTo(20.53f, 11.13f, 21.0f, 12.5f, 21.0f, 14.0f)
            horizontalLineTo(22.0f)
            curveTo(22.55f, 14.0f, 23.0f, 14.45f, 23.0f, 15.0f)
            moveTo(10.0f, 15.5f)
            curveTo(10.0f, 14.12f, 8.88f, 13.0f, 7.5f, 13.0f)
            reflectiveCurveTo(5.0f, 14.12f, 5.0f, 15.5f)
            reflectiveCurveTo(6.12f, 18.0f, 7.5f, 18.0f)
            reflectiveCurveTo(10.0f, 16.88f, 10.0f, 15.5f)
            moveTo(19.0f, 15.5f)
            curveTo(19.0f, 14.12f, 17.88f, 13.0f, 16.5f, 13.0f)
            reflectiveCurveTo(14.0f, 14.12f, 14.0f, 15.5f)
            reflectiveCurveTo(15.12f, 18.0f, 16.5f, 18.0f)
            reflectiveCurveTo(19.0f, 16.88f, 19.0f, 15.5f)
            moveTo(17.0f, 8.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(17.0f)
            verticalLineTo(8.0f)
            close()
        }
    }.build()
}