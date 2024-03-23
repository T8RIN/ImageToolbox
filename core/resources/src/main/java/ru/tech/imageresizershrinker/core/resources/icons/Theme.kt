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

val Icons.Outlined.Theme: ImageVector by lazy {
    Builder(
        name = "Theme", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.0f, 22.0f)
            horizontalLineTo(10.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(22.0f)
            moveTo(2.0f, 22.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(9.0f)
            verticalLineTo(22.0f)
            horizontalLineTo(2.0f)
            moveTo(18.0f, 18.0f)
            verticalLineTo(10.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(18.0f)
            horizontalLineTo(18.0f)
            moveTo(18.0f, 3.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(3.0f)
            moveTo(2.0f, 18.0f)
            verticalLineTo(3.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(18.0f)
            horizontalLineTo(2.0f)
            moveTo(9.0f, 14.56f)
            arcTo(
                3.0f, 3.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 12.0f,
                y1 = 11.56f
            )
            curveTo(12.0f, 9.56f, 9.0f, 6.19f, 9.0f, 6.19f)
            curveTo(9.0f, 6.19f, 6.0f, 9.56f, 6.0f, 11.56f)
            arcTo(
                3.0f, 3.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 9.0f,
                y1 = 14.56f
            )
            close()
        }
    }.build()
}