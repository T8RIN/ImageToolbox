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

val Icons.Rounded.ContentPasteOff: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.ContentPasteOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(800f, 680f)
            quadToRelative(-15f, 0f, -27.5f, -10.5f)
            reflectiveQuadTo(760f, 639f)
            verticalLineToRelative(-439f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 320f)
            lineTo(467f, 320f)
            quadToRelative(-16f, 0f, -30.5f, -6f)
            reflectiveQuadTo(411f, 297f)
            lineTo(302f, 188f)
            quadToRelative(-6f, -6f, -9f, -13f)
            reflectiveQuadToRelative(-3f, -15f)
            quadToRelative(0f, -16f, 11.5f, -28f)
            reflectiveQuadToRelative(29.5f, -12f)
            horizontalLineToRelative(36f)
            quadToRelative(11f, -35f, 43f, -57.5f)
            reflectiveQuadToRelative(70f, -22.5f)
            quadToRelative(40f, 0f, 71.5f, 22.5f)
            reflectiveQuadTo(594f, 120f)
            horizontalLineToRelative(166f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(440f)
            quadToRelative(0f, 20f, -12.5f, 30f)
            reflectiveQuadTo(800f, 680f)
            close()
            moveTo(480f, 200f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(520f, 160f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(480f, 120f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(440f, 160f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(480f, 200f)
            close()
            moveTo(646f, 760f)
            lineTo(200f, 314f)
            verticalLineToRelative(446f)
            horizontalLineToRelative(446f)
            close()
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-526f)
            lineToRelative(-37f, -37f)
            quadToRelative(-12f, -12f, -12f, -28.5f)
            reflectiveQuadTo(83f, 140f)
            quadToRelative(12f, -12f, 28.5f, -12f)
            reflectiveQuadToRelative(28.5f, 12f)
            lineToRelative(680f, 680f)
            quadToRelative(12f, 12f, 12f, 28f)
            reflectiveQuadToRelative(-12f, 28f)
            quadToRelative(-12f, 12f, -28.5f, 12f)
            reflectiveQuadTo(763f, 876f)
            lineToRelative(-37f, -36f)
            lineTo(200f, 840f)
            close()
        }
    }.build()
}
