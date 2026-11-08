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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.Rocket: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Rocket",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(319f, -230f)
                quadToRelative(-10f, -29f, -18.5f, -59f)
                reflectiveQuadTo(287f, -349f)
                lineToRelative(-47f, 32f)
                verticalLineToRelative(119f)
                lineToRelative(79f, -32f)
                close()
                moveToRelative(147f, -535f)
                quadToRelative(-48f, 51f, -77f, 134.5f)
                reflectiveQuadTo(360f, -455f)
                quadToRelative(0f, 60f, 11f, 117.5f)
                reflectiveQuadToRelative(29f, 97.5f)
                horizontalLineToRelative(160f)
                quadToRelative(18f, -40f, 29f, -97.5f)
                reflectiveQuadTo(600f, -455f)
                quadToRelative(0f, -92f, -29f, -175.5f)
                reflectiveQuadTo(494f, -765f)
                quadToRelative(-3f, -3f, -6.5f, -4.5f)
                reflectiveQuadTo(480f, -771f)
                quadToRelative(-4f, 0f, -7.5f, 1.5f)
                reflectiveQuadTo(466f, -765f)
                close()
                moveToRelative(14f, 325f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(400f, -520f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(480f, -600f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(560f, -520f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(480f, -440f)
                close()
                moveToRelative(161f, 210f)
                lineToRelative(79f, 32f)
                verticalLineToRelative(-119f)
                lineToRelative(-47f, -32f)
                quadToRelative(-5f, 30f, -13.5f, 60f)
                reflectiveQuadTo(641f, -230f)
                close()
                moveTo(511f, -856f)
                quadToRelative(84f, 72f, 126.5f, 177f)
                reflectiveQuadTo(680f, -440f)
                lineToRelative(84f, 56f)
                quadToRelative(17f, 11f, 26.5f, 29f)
                reflectiveQuadToRelative(9.5f, 38f)
                verticalLineToRelative(178f)
                quadToRelative(0f, 21f, -17.5f, 33f)
                reflectiveQuadToRelative(-37.5f, 4f)
                lineToRelative(-144f, -58f)
                horizontalLineTo(359f)
                lineToRelative(-144f, 58f)
                quadToRelative(-20f, 8f, -37.5f, -4f)
                reflectiveQuadTo(160f, -139f)
                verticalLineToRelative(-178f)
                quadToRelative(0f, -20f, 9.5f, -38f)
                reflectiveQuadToRelative(26.5f, -29f)
                lineToRelative(84f, -56f)
                quadToRelative(0f, -134f, 42.5f, -239f)
                reflectiveQuadTo(449f, -856f)
                quadToRelative(7f, -5f, 15f, -8f)
                reflectiveQuadToRelative(16f, -3f)
                quadToRelative(8f, 0f, 16f, 3f)
                reflectiveQuadToRelative(15f, 8f)
                close()
            }
        }
    }.build()
}