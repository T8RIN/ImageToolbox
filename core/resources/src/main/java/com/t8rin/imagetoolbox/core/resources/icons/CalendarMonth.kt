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

val Icons.Rounded.CalendarMonth: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.CalendarMonth",
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
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 880f)
            lineTo(200f, 880f)
            close()
            moveTo(200f, 800f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-400f)
            lineTo(200f, 400f)
            verticalLineToRelative(400f)
            close()
            moveTo(480f, 560f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(440f, 520f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(480f, 480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(520f, 520f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 560f)
            close()
            moveTo(320f, 560f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(280f, 520f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(320f, 480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(360f, 520f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(320f, 560f)
            close()
            moveTo(640f, 560f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(600f, 520f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(640f, 480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(680f, 520f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 560f)
            close()
            moveTo(480f, 720f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(440f, 680f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(480f, 640f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(520f, 680f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 720f)
            close()
            moveTo(320f, 720f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(280f, 680f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(320f, 640f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(360f, 680f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(320f, 720f)
            close()
            moveTo(640f, 720f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(600f, 680f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(640f, 640f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(680f, 680f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(640f, 720f)
            close()
        }
    }.build()
}
