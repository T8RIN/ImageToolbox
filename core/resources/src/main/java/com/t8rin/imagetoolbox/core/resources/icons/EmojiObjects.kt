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

val Icons.Outlined.EmojiObjects: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.EmojiObjects",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 880f)
            quadToRelative(-26f, 0f, -47f, -12.5f)
            reflectiveQuadTo(400f, 834f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(320f, 754f)
            verticalLineToRelative(-142f)
            quadToRelative(-59f, -39f, -94.5f, -103f)
            reflectiveQuadTo(190f, 370f)
            quadToRelative(0f, -121f, 84.5f, -205.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(121f, 0f, 205.5f, 84.5f)
            reflectiveQuadTo(770f, 370f)
            quadToRelative(0f, 77f, -35.5f, 140f)
            reflectiveQuadTo(640f, 612f)
            verticalLineToRelative(142f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(560f, 834f)
            quadToRelative(-12f, 21f, -33f, 33.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(400f, 754f)
            horizontalLineToRelative(160f)
            verticalLineToRelative(-36f)
            lineTo(400f, 718f)
            verticalLineToRelative(36f)
            close()
            moveTo(400f, 678f)
            horizontalLineToRelative(160f)
            verticalLineToRelative(-38f)
            lineTo(400f, 640f)
            verticalLineToRelative(38f)
            close()
            moveTo(392f, 560f)
            horizontalLineToRelative(58f)
            verticalLineToRelative(-108f)
            lineToRelative(-67f, -67f)
            quadToRelative(-9f, -9f, -9f, -21f)
            reflectiveQuadToRelative(9f, -21f)
            quadToRelative(9f, -9f, 21f, -9f)
            reflectiveQuadToRelative(21f, 9f)
            lineToRelative(55f, 55f)
            lineToRelative(55f, -55f)
            quadToRelative(9f, -9f, 21f, -9f)
            reflectiveQuadToRelative(21f, 9f)
            quadToRelative(9f, 9f, 9f, 21f)
            reflectiveQuadToRelative(-9f, 21f)
            lineToRelative(-67f, 67f)
            verticalLineToRelative(108f)
            horizontalLineToRelative(58f)
            quadToRelative(54f, -26f, 88f, -76.5f)
            reflectiveQuadTo(690f, 370f)
            quadToRelative(0f, -88f, -61f, -149f)
            reflectiveQuadToRelative(-149f, -61f)
            quadToRelative(-88f, 0f, -149f, 61f)
            reflectiveQuadToRelative(-61f, 149f)
            quadToRelative(0f, 63f, 34f, 113.5f)
            reflectiveQuadToRelative(88f, 76.5f)
            close()
            moveTo(480f, 398f)
            close()
            moveTo(480f, 360f)
            close()
        }
    }.build()
}
