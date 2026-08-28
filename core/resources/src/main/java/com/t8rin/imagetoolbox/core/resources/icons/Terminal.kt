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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.Terminal: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Terminal",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(640f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 240f)
            verticalLineToRelative(480f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 800f)
            lineTo(160f, 800f)
            close()
            moveTo(160f, 720f)
            horizontalLineToRelative(640f)
            verticalLineToRelative(-400f)
            lineTo(160f, 320f)
            verticalLineToRelative(400f)
            close()
            moveTo(347f, 520f)
            lineTo(271f, 444f)
            quadToRelative(-12f, -12f, -11.5f, -28f)
            reflectiveQuadToRelative(12.5f, -28f)
            quadToRelative(12f, -11f, 28f, -11.5f)
            reflectiveQuadToRelative(28f, 11.5f)
            lineToRelative(104f, 104f)
            quadToRelative(12f, 12f, 12f, 28f)
            reflectiveQuadToRelative(-12f, 28f)
            lineTo(328f, 652f)
            quadToRelative(-11f, 11f, -27.5f, 11.5f)
            reflectiveQuadTo(272f, 652f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            lineToRelative(75f, -76f)
            close()
            moveTo(520f, 680f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(480f, 640f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(520f, 600f)
            horizontalLineToRelative(160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(720f, 640f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(680f, 680f)
            lineTo(520f, 680f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Terminal: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.Terminal",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 20f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(6f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(16f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineTo(4f)
            close()
            moveTo(4f, 18f)
            horizontalLineToRelative(16f)
            verticalLineTo(8f)
            horizontalLineTo(4f)
            verticalLineToRelative(10f)
            close()
            moveTo(8.675f, 13f)
            lineToRelative(-1.9f, -1.9f)
            curveToRelative(-0.2f, -0.2f, -0.296f, -0.433f, -0.287f, -0.7f)
            reflectiveCurveToRelative(0.112f, -0.5f, 0.313f, -0.7f)
            curveToRelative(0.2f, -0.183f, 0.433f, -0.279f, 0.7f, -0.287f)
            reflectiveCurveToRelative(0.5f, 0.087f, 0.7f, 0.287f)
            lineToRelative(2.6f, 2.6f)
            curveToRelative(0.2f, 0.2f, 0.3f, 0.433f, 0.3f, 0.7f)
            reflectiveCurveToRelative(-0.1f, 0.5f, -0.3f, 0.7f)
            lineToRelative(-2.6f, 2.6f)
            curveToRelative(-0.183f, 0.183f, -0.412f, 0.279f, -0.688f, 0.287f)
            reflectiveCurveToRelative(-0.512f, -0.087f, -0.712f, -0.287f)
            curveToRelative(-0.183f, -0.183f, -0.275f, -0.417f, -0.275f, -0.7f)
            reflectiveCurveToRelative(0.092f, -0.517f, 0.275f, -0.7f)
            lineToRelative(1.875f, -1.9f)
            close()
            moveTo(13f, 17f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(4f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-4f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(4f, 8f)
            horizontalLineToRelative(16f)
            verticalLineToRelative(10f)
            horizontalLineToRelative(-16f)
            close()
        }
    }.build()
}