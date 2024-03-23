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

val Icons.Rounded.Toolbox: ImageVector by lazy {
    Builder(
        name = "Toolbox", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(18.0f, 16.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(16.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(2.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(16.0f)
            moveTo(20.0f, 8.0f)
            horizontalLineTo(17.0f)
            verticalLineTo(6.0f)
            curveTo(17.0f, 4.9f, 16.1f, 4.0f, 15.0f, 4.0f)
            horizontalLineTo(9.0f)
            curveTo(7.9f, 4.0f, 7.0f, 4.9f, 7.0f, 6.0f)
            verticalLineTo(8.0f)
            horizontalLineTo(4.0f)
            curveTo(2.9f, 8.0f, 2.0f, 8.9f, 2.0f, 10.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(12.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(12.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(10.0f)
            curveTo(22.0f, 8.9f, 21.1f, 8.0f, 20.0f, 8.0f)
            moveTo(15.0f, 8.0f)
            horizontalLineTo(9.0f)
            verticalLineTo(6.0f)
            horizontalLineTo(15.0f)
            verticalLineTo(8.0f)
            close()
        }
    }.build()
}

val Icons.Outlined.Toolbox: ImageVector by lazy {
    Builder(
        name = "Toolbox Outline", defaultWidth = 24.0.dp, defaultHeight
        = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(20.0f, 8.0f)
            horizontalLineTo(17.0f)
            verticalLineTo(6.0f)
            curveTo(17.0f, 4.9f, 16.1f, 4.0f, 15.0f, 4.0f)
            horizontalLineTo(9.0f)
            curveTo(7.9f, 4.0f, 7.0f, 4.9f, 7.0f, 6.0f)
            verticalLineTo(8.0f)
            horizontalLineTo(4.0f)
            curveTo(2.9f, 8.0f, 2.0f, 8.9f, 2.0f, 10.0f)
            verticalLineTo(20.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(10.0f)
            curveTo(22.0f, 8.9f, 21.1f, 8.0f, 20.0f, 8.0f)
            moveTo(9.0f, 6.0f)
            horizontalLineTo(15.0f)
            verticalLineTo(8.0f)
            horizontalLineTo(9.0f)
            verticalLineTo(6.0f)
            moveTo(20.0f, 18.0f)
            horizontalLineTo(4.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(16.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(16.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(18.0f)
            moveTo(18.0f, 13.0f)
            verticalLineTo(12.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(13.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(12.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(13.0f)
            horizontalLineTo(4.0f)
            verticalLineTo(10.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(13.0f)
            horizontalLineTo(18.0f)
            close()
        }
    }.build()
}