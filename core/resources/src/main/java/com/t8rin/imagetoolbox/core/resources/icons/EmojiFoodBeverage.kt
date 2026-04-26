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

val Icons.Outlined.EmojiFoodBeverage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.EmojiFoodBeverage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 840f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 800f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(200f, 760f)
            horizontalLineToRelative(560f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(800f, 800f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(320f, 680f)
            quadToRelative(-66f, 0f, -113f, -47f)
            reflectiveQuadToRelative(-47f, -113f)
            verticalLineToRelative(-311f)
            quadToRelative(0f, -37f, 26f, -63f)
            reflectiveQuadToRelative(63f, -26f)
            horizontalLineToRelative(551f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 200f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 400f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 66f, -47f, 113f)
            reflectiveQuadToRelative(-113f, 47f)
            lineTo(320f, 680f)
            close()
            moveTo(320f, 200f)
            horizontalLineToRelative(320f)
            horizontalLineToRelative(-400f)
            horizontalLineToRelative(80f)
            close()
            moveTo(720f, 320f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-120f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(120f)
            close()
            moveTo(560f, 600f)
            quadToRelative(33f, 0f, 56.5f, -23.5f)
            reflectiveQuadTo(640f, 520f)
            verticalLineToRelative(-320f)
            lineTo(400f, 200f)
            verticalLineToRelative(16f)
            lineToRelative(72f, 58f)
            quadToRelative(2f, 2f, 8f, 16f)
            verticalLineToRelative(170f)
            quadToRelative(0f, 8f, -6f, 14f)
            reflectiveQuadToRelative(-14f, 6f)
            lineTo(300f, 480f)
            quadToRelative(-8f, 0f, -14f, -6f)
            reflectiveQuadToRelative(-6f, -14f)
            verticalLineToRelative(-170f)
            quadToRelative(0f, -2f, 8f, -16f)
            lineToRelative(72f, -58f)
            verticalLineToRelative(-16f)
            lineTo(240f, 200f)
            verticalLineToRelative(320f)
            quadToRelative(0f, 33f, 23.5f, 56.5f)
            reflectiveQuadTo(320f, 600f)
            horizontalLineToRelative(240f)
            close()
            moveTo(360f, 200f)
            horizontalLineToRelative(40f)
            horizontalLineToRelative(-40f)
            close()
        }
    }.build()
}
