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

package com.t8rin.imagetoolbox.core.resources.icons

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
        name = "Crop Small", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(16.4286f, 5.5714f)
            horizontalLineToRelative(-5.7143f)
            horizontalLineTo(9.2128f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(1.5015f)
            horizontalLineToRelative(5.7143f)
            verticalLineToRelative(5.7143f)
            verticalLineToRelative(1.5015f)
            horizontalLineToRelative(2.0f)
            verticalLineToRelative(-1.5015f)
            verticalLineTo(7.5714f)
            curveTo(18.4286f, 6.4669f, 17.5331f, 5.5714f, 16.4286f, 5.5714f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(20.0f, 16.4282f)
            horizontalLineToRelative(-0.5015f)
            horizontalLineToRelative(-2.8389f)
            verticalLineToRelative(4.0E-4f)
            horizontalLineToRelative(-3.3743f)
            verticalLineToRelative(-0.005f)
            horizontalLineTo(7.5714f)
            verticalLineTo(5.5714f)
            horizontalLineTo(7.571f)
            verticalLineTo(4.4164f)
            verticalLineTo(3.9149f)
            curveToRelative(0.0f, -0.5523f, -0.4477f, -1.0f, -1.0f, -1.0f)
            reflectiveCurveToRelative(-1.0f, 0.4477f, -1.0f, 1.0f)
            verticalLineToRelative(0.5015f)
            verticalLineToRelative(1.155f)
            horizontalLineToRelative(-1.0695f)
            horizontalLineTo(4.0f)
            curveToRelative(-0.5523f, 0.0f, -1.0f, 0.4477f, -1.0f, 1.0f)
            reflectiveCurveToRelative(0.4477f, 1.0f, 1.0f, 1.0f)
            horizontalLineToRelative(0.5015f)
            horizontalLineToRelative(1.0699f)
            verticalLineToRelative(0.5715f)
            verticalLineToRelative(0.2162f)
            verticalLineToRelative(1.7838f)
            verticalLineToRelative(0.5664f)
            horizontalLineTo(5.571f)
            verticalLineToRelative(5.7143f)
            curveToRelative(0.0f, 1.1046f, 0.8954f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(0.5171f)
            curveToRelative(0.0188f, 4.0E-4f, 0.0358f, 0.0051f, 0.0548f, 0.0051f)
            horizontalLineToRelative(6.9582f)
            horizontalLineToRelative(0.756f)
            horizontalLineToRelative(0.5719f)
            verticalLineToRelative(0.6154f)
            verticalLineToRelative(0.3693f)
            verticalLineToRelative(0.1699f)
            verticalLineToRelative(0.0764f)
            verticalLineToRelative(0.2553f)
            curveToRelative(0.0f, 0.5523f, 0.4477f, 1.0f, 1.0f, 1.0f)
            reflectiveCurveToRelative(1.0f, -0.4477f, 1.0f, -1.0f)
            verticalLineToRelative(-0.2553f)
            verticalLineToRelative(-0.0764f)
            verticalLineToRelative(-0.1699f)
            verticalLineToRelative(-0.3693f)
            verticalLineToRelative(-0.6154f)
            horizontalLineToRelative(0.0497f)
            verticalLineToRelative(-4.0E-4f)
            horizontalLineToRelative(1.0198f)
            horizontalLineTo(20.0f)
            curveToRelative(0.5523f, 0.0f, 1.0f, -0.4477f, 1.0f, -1.0f)
            reflectiveCurveTo(20.5523f, 16.4282f, 20.0f, 16.4282f)
            close()
        }
    }.build()
}

val Icons.TwoTone.CropSmall: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.CropSmall",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(16.429f, 5.571f)
            horizontalLineToRelative(-7.216f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(7.216f)
            verticalLineToRelative(7.216f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-7.216f)
            curveToRelative(0f, -1.105f, -0.896f, -2f, -2f, -2f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(20f, 16.428f)
            horizontalLineToRelative(-3.34f)
            verticalLineToRelative(0f)
            horizontalLineToRelative(-3.374f)
            verticalLineToRelative(-0.005f)
            horizontalLineToRelative(-5.714f)
            verticalLineTo(5.571f)
            horizontalLineToRelative(-0f)
            verticalLineToRelative(-1.657f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            reflectiveCurveToRelative(-1f, 0.448f, -1f, 1f)
            verticalLineToRelative(1.656f)
            horizontalLineToRelative(-1.571f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            reflectiveCurveToRelative(0.448f, 1f, 1f, 1f)
            horizontalLineToRelative(1.571f)
            verticalLineToRelative(3.138f)
            horizontalLineToRelative(-0f)
            verticalLineToRelative(5.714f)
            curveToRelative(0f, 1.105f, 0.895f, 2f, 2f, 2f)
            horizontalLineToRelative(0.517f)
            curveToRelative(0.019f, 0f, 0.036f, 0.005f, 0.055f, 0.005f)
            horizontalLineToRelative(8.286f)
            verticalLineToRelative(1.486f)
            curveToRelative(0f, 0.552f, 0.448f, 1f, 1f, 1f)
            reflectiveCurveToRelative(1f, -0.448f, 1f, -1f)
            verticalLineToRelative(-1.486f)
            horizontalLineToRelative(0.05f)
            verticalLineToRelative(-0f)
            horizontalLineToRelative(1.521f)
            curveToRelative(0.552f, 0f, 1f, -0.448f, 1f, -1f)
            reflectiveCurveToRelative(-0.448f, -1f, -1f, -1f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(7.571f, 7.571f)
            horizontalLineToRelative(8.857f)
            verticalLineToRelative(8.852f)
            horizontalLineToRelative(-8.857f)
            close()
        }
    }.build()
}