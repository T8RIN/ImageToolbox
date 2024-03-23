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

val Icons.Rounded.Apng: ImageVector by lazy {
    Builder(
        name = "Apng", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(16.0713f, 14.9742f)
            lineToRelative(-1.3831f, 0.0f)
            lineToRelative(-0.9221f, -2.4742f)
            lineToRelative(0.0f, 2.4742f)
            lineToRelative(-1.3831f, 0.0f)
            lineToRelative(0.0f, -5.938f)
            lineToRelative(1.3831f, 0.0f)
            lineToRelative(0.9221f, 2.4741f)
            lineToRelative(0.0f, -2.4741f)
            lineToRelative(1.3831f, 0.0f)
            lineToRelative(0.0f, 5.938f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(20.9922f, 10.5208f)
            horizontalLineTo(18.518f)
            verticalLineToRelative(2.969f)
            horizontalLineToRelative(0.9897f)
            verticalLineToRelative(-1.4845f)
            horizontalLineToRelative(1.4845f)
            verticalLineToRelative(1.6824f)
            curveToRelative(0.0f, 0.6928f, -0.4948f, 1.2866f, -1.2866f, 1.2866f)
            horizontalLineToRelative(-1.2866f)
            curveToRelative(-0.7917f, 0.0f, -1.2866f, -0.6928f, -1.2866f, -1.2866f)
            verticalLineTo(10.4218f)
            curveToRelative(-0.099f, -0.6928f, 0.3959f, -1.3855f, 1.1876f, -1.3855f)
            horizontalLineToRelative(1.2866f)
            curveToRelative(0.7917f, 0.0f, 1.2866f, 0.6928f, 1.2866f, 1.2866f)
            verticalLineToRelative(0.1979f)
            horizontalLineTo(20.9922f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(10.0217f, 9.0258f)
            horizontalLineTo(7.5476f)
            verticalLineToRelative(5.938f)
            horizontalLineToRelative(1.4845f)
            verticalLineToRelative(-1.9793f)
            horizontalLineToRelative(0.9897f)
            curveToRelative(0.7917f, 0.0f, 1.4845f, -0.6928f, 1.4845f, -1.4845f)
            verticalLineToRelative(-0.9897f)
            curveTo(11.5062f, 9.7185f, 10.8134f, 9.0258f, 10.0217f, 9.0258f)
            close()
            moveTo(10.0217f, 11.4999f)
            horizontalLineTo(9.0321f)
            verticalLineToRelative(-0.9897f)
            horizontalLineToRelative(0.9897f)
            verticalLineTo(11.4999f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(5.3746f, 9.0363f)
            horizontalLineTo(4.1912f)
            curveToRelative(-0.6536f, 0.0f, -1.1834f, 0.5299f, -1.1834f, 1.1834f)
            verticalLineToRelative(4.7335f)
            horizontalLineToRelative(1.1834f)
            verticalLineToRelative(-1.9688f)
            horizontalLineToRelative(1.1834f)
            verticalLineToRelative(1.9688f)
            horizontalLineToRelative(1.1834f)
            verticalLineTo(10.2197f)
            curveTo(6.5579f, 9.5661f, 6.0281f, 9.0363f, 5.3746f, 9.0363f)
            close()
            moveTo(4.288f, 10.5208f)
            horizontalLineToRelative(0.9897f)
            verticalLineToRelative(0.9897f)
            horizontalLineTo(4.288f)
            verticalLineTo(10.5208f)
            close()
        }
    }.build()
}