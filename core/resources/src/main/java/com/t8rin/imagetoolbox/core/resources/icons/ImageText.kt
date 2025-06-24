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

val Icons.Outlined.ImageText: ImageVector by lazy {
    Builder(
        name = "ImageText", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(14.4125f, 5.5875f)
            curveTo(14.0208f, 5.1959f, 13.55f, 5.0f, 13.0f, 5.0f)
            horizontalLineTo(3.0f)
            curveTo(2.45f, 5.0f, 1.9792f, 5.1959f, 1.5875f, 5.5875f)
            reflectiveCurveTo(1.0f, 6.45f, 1.0f, 7.0f)
            verticalLineToRelative(10.0f)
            curveToRelative(0.0f, 0.55f, 0.1959f, 1.0208f, 0.5875f, 1.4125f)
            reflectiveCurveTo(2.45f, 19.0f, 3.0f, 19.0f)
            horizontalLineToRelative(10.0f)
            curveToRelative(0.55f, 0.0f, 1.0208f, -0.1959f, 1.4125f, -0.5875f)
            reflectiveCurveTo(15.0f, 17.55f, 15.0f, 17.0f)
            verticalLineTo(7.0f)
            curveTo(15.0f, 6.45f, 14.8041f, 5.9792f, 14.4125f, 5.5875f)
            close()
            moveTo(13.0f, 17.0f)
            horizontalLineTo(3.0f)
            verticalLineTo(7.0f)
            horizontalLineToRelative(10.0f)
            verticalLineTo(17.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(4.0f, 15.0f)
            lineToRelative(8.0f, 0.0f)
            lineToRelative(-2.6f, -3.5f)
            lineToRelative(-1.9f, 2.5f)
            lineToRelative(-1.4f, -1.85f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(3.0f, 17.0f)
            verticalLineToRelative(-10.0f)
            verticalLineTo(17.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(17.0f, 5.1755f)
            horizontalLineToRelative(6.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(-6.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(17.0f, 15.8245f)
            horizontalLineToRelative(6.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(-6.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(16.8404f, 10.5f)
            horizontalLineToRelative(6.0f)
            verticalLineToRelative(3.0f)
            horizontalLineToRelative(-6.0f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ImageText: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    Builder(
        name = "TwoTone.ImageText",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(14.413f, 5.587f)
            curveToRelative(-0.392f, -0.392f, -0.863f, -0.588f, -1.413f, -0.588f)
            horizontalLineTo(3f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.413f, 0.588f)
            reflectiveCurveToRelative(-0.587f, 0.862f, -0.587f, 1.412f)
            verticalLineToRelative(10f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.587f, 1.413f)
            reflectiveCurveToRelative(0.863f, 0.587f, 1.413f, 0.587f)
            horizontalLineToRelative(10f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.413f, -0.587f)
            reflectiveCurveToRelative(0.587f, -0.863f, 0.587f, -1.413f)
            verticalLineTo(7f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.587f, -1.412f)
            close()
            moveTo(13f, 17f)
            horizontalLineTo(3f)
            verticalLineTo(7f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(10f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(3f, 7f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(10f)
            horizontalLineToRelative(-10f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(4f, 15f)
            horizontalLineToRelative(8f)
            lineToRelative(-2.6f, -3.5f)
            lineToRelative(-1.9f, 2.5f)
            lineToRelative(-1.4f, -1.85f)
            lineToRelative(-2.1f, 2.85f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(3f, 17f)
            verticalLineTo(7f)
            verticalLineToRelative(10f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(17f, 5.175f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(-6f)
            verticalLineToRelative(-3f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(17f, 15.824f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(-6f)
            verticalLineToRelative(-3f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(16.84f, 10.5f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(-6f)
            verticalLineToRelative(-3f)
            close()
        }
    }.build()
}