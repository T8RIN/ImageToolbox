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

val Icons.Rounded.FloatingActionButton: ImageVector by lazy {
    Builder(
        name = "Floating Action Button", defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.9692f, 10.67f)
            lineToRelative(-3.0954f, 3.0955f)
            lineToRelative(0.0f, 0.4512f)
            lineToRelative(0.4512f, 0.0f)
            lineToRelative(3.0954f, -3.0954f)
            lineToRelative(-0.2296f, -0.2217f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(15.8593f, 3.1335f)
            horizontalLineTo(8.1407f)
            curveTo(5.3016f, 3.1335f, 3.0f, 5.4351f, 3.0f, 8.2742f)
            verticalLineToRelative(7.7186f)
            curveToRelative(0.0f, 2.8392f, 2.3016f, 5.1407f, 5.1407f, 5.1407f)
            horizontalLineToRelative(7.7186f)
            curveToRelative(2.8391f, 0.0f, 5.1407f, -2.3015f, 5.1407f, -5.1407f)
            verticalLineTo(8.2742f)
            curveTo(21.0f, 5.4351f, 18.6984f, 3.1335f, 15.8593f, 3.1335f)
            close()
            moveTo(14.7643f, 10.672f)
            curveToRelative(-0.001f, 0.001f, -0.0026f, 0.0013f, -0.0036f, 0.0024f)
            curveToRelative(-0.001f, 0.0011f, -0.0012f, 0.0024f, -0.0023f, 0.0035f)
            lineToRelative(-4.1721f, 4.1721f)
            horizontalLineTo(9.2404f)
            verticalLineToRelative(-1.3458f)
            lineToRelative(4.18f, -4.1721f)
            curveToRelative(4.0E-4f, -3.0E-4f, 8.0E-4f, -4.0E-4f, 0.0012f, -7.0E-4f)
            curveToRelative(4.0E-4f, -4.0E-4f, 4.0E-4f, -9.0E-4f, 8.0E-4f, -0.0012f)
            curveToRelative(0.2612f, -0.2612f, 0.6848f, -0.2612f, 0.946f, 0.0f)
            lineToRelative(0.3959f, 0.3959f)
            curveTo(15.0255f, 9.9872f, 15.0255f, 10.4108f, 14.7643f, 10.672f)
            close()
        }
    }.build()
}