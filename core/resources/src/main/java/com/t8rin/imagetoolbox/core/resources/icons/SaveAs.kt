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

val Icons.Outlined.SaveAs: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SaveAs",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(447f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(25.5f, 17f)
            lineToRelative(114f, 114f)
            quadToRelative(11f, 11f, 17f, 25.5f)
            reflectiveQuadToRelative(6f, 30.5f)
            verticalLineToRelative(127f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 480f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(760f, 440f)
            verticalLineToRelative(-127f)
            lineTo(647f, 200f)
            lineTo(200f, 200f)
            verticalLineToRelative(560f)
            horizontalLineToRelative(200f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(440f, 800f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(400f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(200f, 200f)
            verticalLineToRelative(560f)
            verticalLineToRelative(-560f)
            close()
            moveTo(520f, 880f)
            verticalLineToRelative(-66f)
            quadToRelative(0f, -8f, 3f, -15.5f)
            reflectiveQuadToRelative(9f, -13.5f)
            lineToRelative(209f, -208f)
            quadToRelative(9f, -9f, 20f, -13f)
            reflectiveQuadToRelative(22f, -4f)
            quadToRelative(12f, 0f, 23f, 4.5f)
            reflectiveQuadToRelative(20f, 13.5f)
            lineToRelative(37f, 37f)
            quadToRelative(8f, 9f, 12.5f, 20f)
            reflectiveQuadToRelative(4.5f, 22f)
            quadToRelative(0f, 11f, -4f, 22.5f)
            reflectiveQuadTo(863f, 700f)
            lineTo(655f, 908f)
            quadToRelative(-6f, 6f, -13.5f, 9f)
            reflectiveQuadTo(626f, 920f)
            horizontalLineToRelative(-66f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 880f)
            close()
            moveTo(820f, 657f)
            lineTo(783f, 620f)
            lineTo(820f, 657f)
            close()
            moveTo(580f, 860f)
            horizontalLineToRelative(38f)
            lineToRelative(121f, -122f)
            lineToRelative(-18f, -19f)
            lineToRelative(-19f, -18f)
            lineToRelative(-122f, 121f)
            verticalLineToRelative(38f)
            close()
            moveTo(721f, 719f)
            lineTo(702f, 701f)
            lineTo(739f, 738f)
            lineTo(721f, 719f)
            close()
            moveTo(280f, 400f)
            horizontalLineToRelative(280f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(600f, 360f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(560f, 240f)
            lineTo(280f, 240f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(240f, 280f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(280f, 400f)
            close()
            moveTo(480f, 720f)
            horizontalLineToRelative(4f)
            lineToRelative(116f, -115f)
            verticalLineToRelative(-5f)
            quadToRelative(0f, -50f, -35f, -85f)
            reflectiveQuadToRelative(-85f, -35f)
            quadToRelative(-50f, 0f, -85f, 35f)
            reflectiveQuadToRelative(-35f, 85f)
            quadToRelative(0f, 50f, 35f, 85f)
            reflectiveQuadToRelative(85f, 35f)
            close()
        }
    }.build()
}
