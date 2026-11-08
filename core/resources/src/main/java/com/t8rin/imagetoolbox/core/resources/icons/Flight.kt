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

val Icons.Outlined.Flight: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Flight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, -120f)
                lineTo(377f, -91f)
                quadToRelative(-14f, 4f, -25.5f, -4.5f)
                reflectiveQuadTo(340f, -118f)
                quadToRelative(0f, -12f, 3f, -19.5f)
                reflectiveQuadToRelative(8f, -11.5f)
                lineToRelative(69f, -51f)
                verticalLineToRelative(-220f)
                lineToRelative(-291f, 86f)
                quadToRelative(-19f, 5f, -34f, -6f)
                reflectiveQuadToRelative(-15f, -31f)
                quadToRelative(0f, -15f, 5f, -25f)
                reflectiveQuadToRelative(14f, -15f)
                lineToRelative(321f, -189f)
                verticalLineToRelative(-220f)
                quadToRelative(0f, -25f, 17.5f, -42.5f)
                reflectiveQuadTo(480f, -880f)
                quadToRelative(25f, 0f, 42.5f, 17.5f)
                reflectiveQuadTo(540f, -820f)
                verticalLineToRelative(220f)
                lineToRelative(321f, 189f)
                quadToRelative(9f, 5f, 14f, 15f)
                reflectiveQuadToRelative(5f, 25f)
                quadToRelative(0f, 20f, -15f, 31f)
                reflectiveQuadToRelative(-34f, 6f)
                lineToRelative(-291f, -86f)
                verticalLineToRelative(220f)
                lineToRelative(69f, 51f)
                quadToRelative(5f, 4f, 8f, 11.5f)
                reflectiveQuadToRelative(3f, 19.5f)
                quadToRelative(0f, 14f, -11.5f, 22.5f)
                reflectiveQuadTo(583f, -91f)
                lineToRelative(-103f, -29f)
                close()
            }
        }
    }.build()
}