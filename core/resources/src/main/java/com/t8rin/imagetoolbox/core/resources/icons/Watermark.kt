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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Watermark: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Watermark",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(11f, 17f)
            horizontalLineToRelative(7f)
            curveToRelative(0.283f, 0f, 0.521f, -0.096f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.287f, -0.429f, 0.287f, -0.712f)
            verticalLineToRelative(-4f)
            curveToRelative(0f, -0.283f, -0.096f, -0.521f, -0.287f, -0.712f)
            curveToRelative(-0.192f, -0.192f, -0.429f, -0.287f, -0.712f, -0.287f)
            horizontalLineToRelative(-7f)
            curveToRelative(-0.283f, 0f, -0.521f, 0.096f, -0.712f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.712f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.283f, 0.096f, 0.521f, 0.287f, 0.712f)
            reflectiveCurveToRelative(0.429f, 0.287f, 0.712f, 0.287f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.412f, 4.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(4f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(16f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineTo(6f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
            moveTo(20f, 17.398f)
            curveToRelative(0f, 0.333f, -0.27f, 0.602f, -0.602f, 0.602f)
            horizontalLineTo(4.602f)
            curveToRelative(-0.333f, 0f, -0.602f, -0.27f, -0.602f, -0.602f)
            verticalLineTo(6.602f)
            curveToRelative(0f, -0.333f, 0.27f, -0.602f, 0.602f, -0.602f)
            horizontalLineToRelative(14.795f)
            curveToRelative(0.333f, 0f, 0.602f, 0.27f, 0.602f, 0.602f)
            verticalLineToRelative(10.795f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 18f)
            verticalLineTo(6f)
            verticalLineToRelative(12f)
            close()
        }
    }.build()
}


val Icons.TwoTone.Watermark: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoToneWatermark",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.412f, 4.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(4f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(16f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineTo(6f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
            moveTo(20f, 17.398f)
            curveToRelative(0f, 0.333f, -0.27f, 0.602f, -0.602f, 0.602f)
            horizontalLineTo(4.602f)
            curveToRelative(-0.333f, 0f, -0.602f, -0.27f, -0.602f, -0.602f)
            verticalLineTo(6.602f)
            curveToRelative(0f, -0.333f, 0.27f, -0.602f, 0.602f, -0.602f)
            horizontalLineToRelative(14.795f)
            curveToRelative(0.333f, 0f, 0.602f, 0.27f, 0.602f, 0.602f)
            verticalLineToRelative(10.795f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(11f, 17f)
            horizontalLineToRelative(7f)
            curveToRelative(0.283f, 0f, 0.521f, -0.096f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.287f, -0.429f, 0.287f, -0.712f)
            verticalLineToRelative(-4f)
            curveToRelative(0f, -0.283f, -0.096f, -0.521f, -0.287f, -0.712f)
            curveToRelative(-0.192f, -0.192f, -0.429f, -0.287f, -0.712f, -0.287f)
            horizontalLineToRelative(-7f)
            curveToRelative(-0.283f, 0f, -0.521f, 0.096f, -0.712f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.712f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.283f, 0.096f, 0.521f, 0.287f, 0.712f)
            reflectiveCurveToRelative(0.429f, 0.287f, 0.712f, 0.287f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(4.602f, 6f)
            lineTo(19.398f, 6f)
            arcTo(0.602f, 0.602f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 6.602f)
            lineTo(20f, 17.398f)
            arcTo(0.602f, 0.602f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19.398f, 18f)
            lineTo(4.602f, 18f)
            arcTo(0.602f, 0.602f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 17.398f)
            lineTo(4f, 6.602f)
            arcTo(0.602f, 0.602f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.602f, 6f)
            close()
        }
    }.build()
}
