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

val Icons.Outlined.EditCalendar: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.EditCalendar",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 800f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 160f)
            horizontalLineToRelative(40f)
            verticalLineToRelative(-40f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(280f, 80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(320f, 120f)
            verticalLineToRelative(40f)
            horizontalLineToRelative(320f)
            verticalLineToRelative(-40f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(680f, 80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(720f, 120f)
            verticalLineToRelative(40f)
            horizontalLineToRelative(40f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 240f)
            verticalLineToRelative(161f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 441f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(760f, 401f)
            verticalLineToRelative(-1f)
            lineTo(200f, 400f)
            verticalLineToRelative(400f)
            horizontalLineToRelative(240f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(480f, 840f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(440f, 880f)
            lineTo(200f, 880f)
            close()
            moveTo(200f, 320f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-80f)
            lineTo(200f, 240f)
            verticalLineToRelative(80f)
            close()
            moveTo(200f, 320f)
            verticalLineToRelative(-80f)
            verticalLineToRelative(80f)
            close()
            moveTo(560f, 840f)
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
            reflectiveQuadTo(903f, 660f)
            lineTo(695f, 868f)
            quadToRelative(-6f, 6f, -13.5f, 9f)
            reflectiveQuadTo(666f, 880f)
            horizontalLineToRelative(-66f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(560f, 840f)
            close()
            moveTo(860f, 617f)
            lineTo(823f, 580f)
            lineTo(860f, 617f)
            close()
            moveTo(620f, 820f)
            horizontalLineToRelative(38f)
            lineToRelative(121f, -122f)
            lineToRelative(-18f, -19f)
            lineToRelative(-19f, -18f)
            lineToRelative(-122f, 121f)
            verticalLineToRelative(38f)
            close()
            moveTo(761f, 679f)
            lineTo(742f, 661f)
            lineTo(779f, 698f)
            lineTo(761f, 679f)
            close()
        }
    }.build()
}
