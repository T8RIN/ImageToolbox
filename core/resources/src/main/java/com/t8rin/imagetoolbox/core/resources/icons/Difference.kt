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

val Icons.Outlined.Difference: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Difference",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(500f, 360f)
            verticalLineToRelative(40f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(540f, 440f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(580f, 400f)
            verticalLineToRelative(-40f)
            horizontalLineToRelative(40f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(660f, 320f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(620f, 280f)
            horizontalLineToRelative(-40f)
            verticalLineToRelative(-40f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(540f, 200f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(500f, 240f)
            verticalLineToRelative(40f)
            horizontalLineToRelative(-40f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(420f, 320f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(460f, 360f)
            horizontalLineToRelative(40f)
            close()
            moveTo(460f, 600f)
            horizontalLineToRelative(160f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(660f, 560f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(620f, 520f)
            lineTo(460f, 520f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(420f, 560f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(460f, 600f)
            close()
            moveTo(320f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(240f, 680f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(320f, 40f)
            horizontalLineToRelative(247f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(25.5f, 17f)
            lineToRelative(194f, 194f)
            quadToRelative(11f, 11f, 17f, 25.5f)
            reflectiveQuadToRelative(6f, 30.5f)
            verticalLineToRelative(367f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 760f)
            lineTo(320f, 760f)
            close()
            moveTo(320f, 680f)
            horizontalLineToRelative(440f)
            verticalLineToRelative(-360f)
            lineTo(560f, 120f)
            lineTo(320f, 120f)
            verticalLineToRelative(560f)
            close()
            moveTo(160f, 920f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 840f)
            verticalLineToRelative(-520f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 280f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(160f, 320f)
            verticalLineToRelative(520f)
            horizontalLineToRelative(400f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(600f, 880f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(560f, 920f)
            lineTo(160f, 920f)
            close()
            moveTo(320f, 680f)
            verticalLineToRelative(-560f)
            verticalLineToRelative(560f)
            close()
        }
    }.build()
}
