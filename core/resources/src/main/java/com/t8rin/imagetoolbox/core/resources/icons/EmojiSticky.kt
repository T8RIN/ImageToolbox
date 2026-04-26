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

val Icons.Outlined.EmojiSticky: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.EmojiSticky",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(460f, 600f)
            quadToRelative(64f, 0f, 113.5f, -39.5f)
            reflectiveQuadTo(637f, 460f)
            quadToRelative(2f, -6f, -3f, -10.5f)
            reflectiveQuadToRelative(-11f, -2.5f)
            lineToRelative(-282f, 79f)
            quadToRelative(-8f, 2f, -9.5f, 9.5f)
            reflectiveQuadTo(335f, 548f)
            quadToRelative(25f, 24f, 57f, 38f)
            reflectiveQuadToRelative(68f, 14f)
            close()
            moveTo(294f, 450f)
            lineToRelative(106f, -30f)
            quadToRelative(4f, -28f, -14f, -49f)
            reflectiveQuadToRelative(-46f, -21f)
            quadToRelative(-25f, 0f, -42.5f, 17.5f)
            reflectiveQuadTo(280f, 410f)
            quadToRelative(0f, 11f, 4f, 21f)
            reflectiveQuadToRelative(10f, 19f)
            close()
            moveTo(534f, 380f)
            lineTo(640f, 350f)
            quadToRelative(5f, -28f, -13.5f, -49f)
            reflectiveQuadTo(580f, 280f)
            quadToRelative(-25f, 0f, -42.5f, 17.5f)
            reflectiveQuadTo(520f, 340f)
            quadToRelative(0f, 11f, 4f, 21f)
            reflectiveQuadToRelative(10f, 19f)
            close()
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(407f)
            quadToRelative(0f, 16f, -6f, 30.5f)
            reflectiveQuadTo(817f, 663f)
            lineTo(663f, 817f)
            quadToRelative(-11f, 11f, -25.5f, 17f)
            reflectiveQuadToRelative(-30.5f, 6f)
            lineTo(200f, 840f)
            close()
            moveTo(600f, 760f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(680f, 600f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-400f)
            lineTo(200f, 200f)
            verticalLineToRelative(560f)
            horizontalLineToRelative(400f)
            close()
            moveTo(600f, 760f)
            close()
            moveTo(200f, 760f)
            verticalLineToRelative(-560f)
            verticalLineToRelative(560f)
            close()
        }
    }.build()
}
