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

val Icons.Outlined.Jpg: ImageVector by lazy {
    Builder(
        name = "Jpg", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth
        = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(8.1429f, 13.9286f)
            curveToRelative(0.0f, 1.4143f, -1.1571f, 1.9286f, -2.5714f, 1.9286f)
            reflectiveCurveTo(3.0f, 15.3429f, 3.0f, 13.9286f)
            verticalLineTo(12.0f)
            horizontalLineToRelative(1.9286f)
            verticalLineToRelative(1.9286f)
            horizontalLineToRelative(1.2857f)
            verticalLineTo(8.1429f)
            horizontalLineToRelative(1.9286f)
            verticalLineTo(13.9286f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(21.0f, 10.0714f)
            horizontalLineToRelative(-3.2143f)
            verticalLineToRelative(3.8571f)
            horizontalLineToRelative(1.2857f)
            verticalLineTo(12.0f)
            horizontalLineTo(21.0f)
            verticalLineToRelative(2.1857f)
            curveToRelative(0.0f, 0.9f, -0.6429f, 1.6714f, -1.6714f, 1.6714f)
            horizontalLineToRelative(-1.6714f)
            curveToRelative(-1.0286f, 0.0f, -1.6714f, -0.9f, -1.6714f, -1.6714f)
            verticalLineToRelative(-4.2429f)
            curveTo(15.8571f, 9.0429f, 16.5f, 8.1429f, 17.5286f, 8.1429f)
            horizontalLineToRelative(1.6714f)
            curveToRelative(1.0286f, 0.0f, 1.6714f, 0.9f, 1.6714f, 1.6714f)
            verticalLineToRelative(0.2571f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.6429f, 8.1429f)
            horizontalLineTo(9.4286f)
            verticalLineToRelative(7.7142f)
            horizontalLineToRelative(1.9285f)
            verticalLineToRelative(-2.5714f)
            horizontalLineToRelative(1.2858f)
            curveToRelative(1.0286f, 0.0f, 1.9285f, -0.9f, 1.9285f, -1.9286f)
            verticalLineToRelative(-1.2857f)
            curveTo(14.5714f, 9.0428f, 13.6714f, 8.1429f, 12.6429f, 8.1429f)
            close()
            moveTo(12.6429f, 11.3571f)
            horizontalLineToRelative(-1.2858f)
            verticalLineToRelative(-1.2857f)
            horizontalLineToRelative(1.2858f)
            verticalLineTo(11.3571f)
            close()
        }
    }.build()
}
