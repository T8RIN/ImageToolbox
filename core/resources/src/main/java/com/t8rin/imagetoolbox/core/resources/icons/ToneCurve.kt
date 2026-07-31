/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.ToneCurve: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ToneCurve",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.412f, 3.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(5f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(14f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineTo(5f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
            moveTo(19f, 19f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(14f)
            close()
        }
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(7.5f, 16.5f)
            curveToRelative(6.446f, 0f, 2.355f, -9f, 9f, -9f)
        }
    }.build()
}

val Icons.TwoTone.ToneCurve: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ToneCurve",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.412f, 3.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(5f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(14f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineTo(5f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
            moveTo(19f, 19f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(14f)
            close()
        }
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(7.5f, 16.5f)
            curveToRelative(6.446f, 0f, 2.355f, -9f, 9f, -9f)
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5f, 5f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(-14f)
            close()
        }
    }.build()
}

val Icons.Rounded.ToneCurve: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.ToneCurve",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.412f, 3.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(5f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(14f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineTo(5f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
            moveTo(16.5f, 8.5f)
            curveToRelative(-2.464f, 0f, -2.96f, 1.378f, -3.624f, 3.814f)
            curveToRelative(-0.597f, 2.188f, -1.414f, 5.186f, -5.376f, 5.186f)
            curveToRelative(-0.552f, 0f, -1f, -0.447f, -1f, -1f)
            reflectiveCurveToRelative(0.448f, -1f, 1f, -1f)
            curveToRelative(2.326f, 0f, 2.781f, -1.272f, 3.446f, -3.712f)
            curveToRelative(0.609f, -2.231f, 1.442f, -5.288f, 5.554f, -5.288f)
            curveToRelative(0.553f, 0f, 1f, 0.448f, 1f, 1f)
            reflectiveCurveToRelative(-0.447f, 1f, -1f, 1f)
            close()
        }
    }.build()
}