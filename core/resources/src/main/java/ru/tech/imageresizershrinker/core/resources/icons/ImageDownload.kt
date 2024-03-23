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

val Icons.Outlined.ImageDownload: ImageVector by lazy {
    Builder(
        name = "ImageDownload", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(15.0f, 19.0f)
            curveToRelative(0.0f, -0.5523f, -0.4477f, -1.0f, -1.0f, -1.0f)
            horizontalLineToRelative(-1.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(4.0f)
            curveToRelative(1.1046f, 0.0f, 2.0f, -0.8954f, 2.0f, -2.0f)
            verticalLineTo(4.0f)
            curveToRelative(0.0f, -1.1046f, -0.8954f, -2.0f, -2.0f, -2.0f)
            horizontalLineTo(7.0f)
            curveTo(5.8954f, 2.0f, 5.0f, 2.8954f, 5.0f, 4.0f)
            verticalLineToRelative(10.0f)
            curveToRelative(0.0f, 1.1046f, 0.8954f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(4.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(-1.0f)
            curveToRelative(-0.5523f, 0.0f, -1.0f, 0.4477f, -1.0f, 1.0f)
            horizontalLineTo(2.0f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(7.0f)
            curveToRelative(0.0f, 0.5523f, 0.4477f, 1.0f, 1.0f, 1.0f)
            horizontalLineToRelative(4.0f)
            curveToRelative(0.5523f, 0.0f, 1.0f, -0.4477f, 1.0f, -1.0f)
            horizontalLineToRelative(7.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineTo(15.0f)
            close()
            moveTo(7.0f, 14.0f)
            verticalLineTo(4.0f)
            horizontalLineToRelative(10.0f)
            verticalLineToRelative(10.0f)
            horizontalLineTo(7.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(13.343f, 9.0f)
            lineToRelative(-1.8843f, 2.4256f)
            lineToRelative(-1.343f, -1.6171f)
            lineToRelative(-1.8843f, 2.4188f)
            lineToRelative(7.5373f, 0.0f)
            close()
        }
    }.build()
}