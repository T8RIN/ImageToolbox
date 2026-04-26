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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.ScreenRotationAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.ScreenRotationAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(487f, 856f)
            lineTo(219f, 589f)
            quadToRelative(-6f, -6f, -9f, -13f)
            reflectiveQuadToRelative(-3f, -15f)
            quadToRelative(0f, -16f, 11f, -28.5f)
            reflectiveQuadToRelative(29f, -12.5f)
            quadToRelative(8f, 0f, 15.5f, 3f)
            reflectiveQuadToRelative(13.5f, 9f)
            lineToRelative(268f, 268f)
            lineToRelative(200f, -200f)
            horizontalLineToRelative(-64f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(640f, 560f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(680f, 520f)
            horizontalLineToRelative(160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 560f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 760f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(800f, 720f)
            verticalLineToRelative(-64f)
            lineTo(600f, 856f)
            quadToRelative(-11f, 11f, -25.5f, 17f)
            reflectiveQuadTo(544f, 879f)
            quadToRelative(-15f, 0f, -30f, -6f)
            reflectiveQuadToRelative(-27f, -17f)
            close()
            moveTo(120f, 440f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(80f, 400f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 200f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(160f, 240f)
            verticalLineToRelative(64f)
            lineToRelative(200f, -200f)
            quadToRelative(12f, -12f, 27f, -17.5f)
            reflectiveQuadToRelative(30f, -5.5f)
            quadToRelative(16f, 0f, 30.5f, 5.5f)
            reflectiveQuadTo(473f, 104f)
            lineToRelative(268f, 267f)
            quadToRelative(6f, 6f, 9f, 13f)
            reflectiveQuadToRelative(3f, 15f)
            quadToRelative(0f, 16f, -11f, 28.5f)
            reflectiveQuadTo(713f, 440f)
            quadToRelative(-8f, 0f, -15.5f, -3f)
            reflectiveQuadToRelative(-13.5f, -9f)
            lineTo(416f, 160f)
            lineTo(216f, 360f)
            horizontalLineToRelative(64f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(320f, 400f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(280f, 440f)
            lineTo(120f, 440f)
            close()
        }
    }.build()
}
