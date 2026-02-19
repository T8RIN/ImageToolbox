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

val Icons.Outlined.DeleteSweep: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.DeleteSweep",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(600f, 720f)
            lineTo(600f, 640f)
            lineTo(760f, 640f)
            lineTo(760f, 720f)
            lineTo(600f, 720f)
            close()
            moveTo(600f, 400f)
            lineTo(600f, 320f)
            lineTo(880f, 320f)
            lineTo(880f, 400f)
            lineTo(600f, 400f)
            close()
            moveTo(600f, 560f)
            lineTo(600f, 480f)
            lineTo(840f, 480f)
            lineTo(840f, 560f)
            lineTo(600f, 560f)
            close()
            moveTo(120f, 320f)
            lineTo(80f, 320f)
            lineTo(80f, 240f)
            lineTo(240f, 240f)
            lineTo(240f, 180f)
            lineTo(400f, 180f)
            lineTo(400f, 240f)
            lineTo(560f, 240f)
            lineTo(560f, 320f)
            lineTo(520f, 320f)
            lineTo(520f, 680f)
            quadTo(520f, 713f, 496.5f, 736.5f)
            quadTo(473f, 760f, 440f, 760f)
            lineTo(200f, 760f)
            quadTo(167f, 760f, 143.5f, 736.5f)
            quadTo(120f, 713f, 120f, 680f)
            lineTo(120f, 320f)
            close()
            moveTo(200f, 320f)
            lineTo(200f, 680f)
            quadTo(200f, 680f, 200f, 680f)
            quadTo(200f, 680f, 200f, 680f)
            lineTo(440f, 680f)
            quadTo(440f, 680f, 440f, 680f)
            quadTo(440f, 680f, 440f, 680f)
            lineTo(440f, 320f)
            lineTo(200f, 320f)
            close()
            moveTo(200f, 320f)
            lineTo(200f, 320f)
            lineTo(200f, 680f)
            quadTo(200f, 680f, 200f, 680f)
            quadTo(200f, 680f, 200f, 680f)
            lineTo(200f, 680f)
            quadTo(200f, 680f, 200f, 680f)
            quadTo(200f, 680f, 200f, 680f)
            lineTo(200f, 320f)
            close()
        }
    }.build()
}

val Icons.TwoTone.DeleteSweep: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoToneDeleteSweep",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(15f, 16f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-4f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(15f, 8f)
            horizontalLineToRelative(7f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-7f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(15f, 12f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-6f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(10f, 6f)
            verticalLineToRelative(-1.5f)
            horizontalLineToRelative(-4f)
            verticalLineToRelative(1.5f)
            horizontalLineTo(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(1f)
            verticalLineToRelative(9f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(6f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineToRelative(-9f)
            horizontalLineToRelative(1f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-4f)
            close()
            moveTo(11f, 17f)
            horizontalLineToRelative(-6f)
            verticalLineToRelative(-9f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(9f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 8f)
            verticalLineToRelative(9f)
            verticalLineTo(8f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5f, 8f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(9f)
            horizontalLineToRelative(-6f)
            close()
        }
    }.build()
}
