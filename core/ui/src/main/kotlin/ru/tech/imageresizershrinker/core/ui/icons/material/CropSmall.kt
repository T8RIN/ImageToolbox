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

package ru.tech.imageresizershrinker.core.ui.icons.material

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

val Icons.Rounded.CropSmall: ImageVector by lazy {
    Builder(
        name = "Crop", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(8.1429f, 18.4286f)
            curveToRelative(-0.7071f, 0.0f, -1.3125f, -0.2518f, -1.8161f, -0.7554f)
            reflectiveCurveToRelative(-0.7554f, -1.1089f, -0.7554f, -1.8161f)
            verticalLineTo(8.1429f)
            horizontalLineTo(4.2857f)
            curveToRelative(-0.3643f, 0.0f, -0.6696f, -0.1232f, -0.9161f, -0.3696f)
            reflectiveCurveTo(3.0f, 7.2214f, 3.0f, 6.8571f)
            reflectiveCurveToRelative(0.1232f, -0.6696f, 0.3696f, -0.9161f)
            curveToRelative(0.2464f, -0.2464f, 0.5518f, -0.3696f, 0.9161f, -0.3696f)
            horizontalLineToRelative(1.2857f)
            verticalLineTo(4.2857f)
            curveToRelative(0.0f, -0.3643f, 0.1232f, -0.6696f, 0.3696f, -0.9161f)
            curveTo(6.1875f, 3.1232f, 6.4929f, 3.0f, 6.8571f, 3.0f)
            reflectiveCurveToRelative(0.6696f, 0.1232f, 0.9161f, 0.3696f)
            reflectiveCurveToRelative(0.3696f, 0.5518f, 0.3696f, 0.9161f)
            verticalLineToRelative(11.5714f)
            horizontalLineToRelative(11.5714f)
            curveToRelative(0.3643f, 0.0f, 0.6696f, 0.1232f, 0.9161f, 0.3696f)
            curveTo(20.8768f, 16.4732f, 21.0f, 16.7786f, 21.0f, 17.1429f)
            reflectiveCurveToRelative(-0.1232f, 0.6696f, -0.3696f, 0.9161f)
            curveToRelative(-0.2464f, 0.2464f, -0.5518f, 0.3696f, -0.9161f, 0.3696f)
            horizontalLineToRelative(-1.2857f)
            verticalLineToRelative(1.2857f)
            curveToRelative(0.0f, 0.3643f, -0.1232f, 0.6696f, -0.3696f, 0.9161f)
            curveTo(17.8125f, 20.8768f, 17.5071f, 21.0f, 17.1429f, 21.0f)
            curveToRelative(-0.3643f, 0.0f, -0.6696f, -0.1232f, -0.9161f, -0.3696f)
            curveToRelative(-0.2464f, -0.2464f, -0.3696f, -0.5518f, -0.3696f, -0.9161f)
            verticalLineToRelative(-1.2857f)
            horizontalLineTo(8.1429f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(15.8571f, 13.2857f)
            verticalLineTo(8.1429f)
            horizontalLineToRelative(-5.1429f)
            verticalLineTo(5.5714f)
            horizontalLineToRelative(5.1429f)
            curveToRelative(0.7071f, 0.0f, 1.3125f, 0.2518f, 1.8161f, 0.7554f)
            reflectiveCurveToRelative(0.7554f, 1.1089f, 0.7554f, 1.8161f)
            verticalLineToRelative(5.1429f)
            horizontalLineTo(15.8571f)
            close()
        }
    }.build()
}