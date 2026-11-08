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

val Icons.Outlined.Cyclone: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Cyclone",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, -320f)
                quadToRelative(-66f, 0f, -113f, -47f)
                reflectiveQuadToRelative(-47f, -113f)
                quadToRelative(0f, -66f, 47f, -113f)
                reflectiveQuadToRelative(113f, -47f)
                quadToRelative(66f, 0f, 113f, 47f)
                reflectiveQuadToRelative(47f, 113f)
                quadToRelative(0f, 66f, -47f, 113f)
                reflectiveQuadToRelative(-113f, 47f)
                close()
                moveToRelative(0f, -80f)
                quadToRelative(33f, 0f, 56.5f, -23.5f)
                reflectiveQuadTo(560f, -480f)
                quadToRelative(0f, -33f, -23.5f, -56.5f)
                reflectiveQuadTo(480f, -560f)
                quadToRelative(-33f, 0f, -56.5f, 23.5f)
                reflectiveQuadTo(400f, -480f)
                quadToRelative(0f, 33f, 23.5f, 56.5f)
                reflectiveQuadTo(480f, -400f)
                close()
                moveToRelative(0f, 240f)
                quadToRelative(-114f, 0f, -203.5f, -13.5f)
                reflectiveQuadTo(125f, -202f)
                quadToRelative(-20f, -5f, -32.5f, -19.5f)
                reflectiveQuadTo(80f, -256f)
                quadToRelative(0f, -16f, 13.5f, -25.5f)
                reflectiveQuadTo(123f, -286f)
                quadToRelative(42f, 11f, 74.5f, 17.5f)
                lineTo(250f, -258f)
                quadToRelative(-42f, -43f, -66f, -100f)
                reflectiveQuadToRelative(-24f, -122f)
                quadToRelative(0f, -114f, 13.5f, -203.5f)
                reflectiveQuadTo(202f, -835f)
                quadToRelative(5f, -20f, 19.5f, -32.5f)
                reflectiveQuadTo(256f, -880f)
                quadToRelative(16f, 0f, 25.5f, 13.5f)
                reflectiveQuadTo(286f, -837f)
                quadToRelative(-12f, 42f, -18.5f, 74.5f)
                reflectiveQuadTo(258f, -710f)
                quadToRelative(43f, -42f, 100f, -66f)
                reflectiveQuadToRelative(122f, -24f)
                quadToRelative(114f, 0f, 203.5f, 13.5f)
                reflectiveQuadTo(835f, -758f)
                quadToRelative(20f, 5f, 32.5f, 19.5f)
                reflectiveQuadTo(880f, -704f)
                quadToRelative(0f, 16f, -13.5f, 25.5f)
                reflectiveQuadTo(837f, -674f)
                quadToRelative(-42f, -12f, -74.5f, -18.5f)
                reflectiveQuadTo(710f, -702f)
                quadToRelative(42f, 43f, 66f, 100f)
                reflectiveQuadToRelative(24f, 122f)
                quadToRelative(0f, 114f, -13.5f, 203.5f)
                reflectiveQuadTo(758f, -125f)
                quadToRelative(-5f, 20f, -19.5f, 32.5f)
                reflectiveQuadTo(704f, -80f)
                quadToRelative(-16f, 0f, -25.5f, -13.5f)
                reflectiveQuadTo(674f, -123f)
                quadToRelative(11f, -42f, 17.5f, -74.5f)
                lineTo(702f, -250f)
                quadToRelative(-43f, 42f, -100f, 66f)
                reflectiveQuadToRelative(-122f, 24f)
                close()
                moveToRelative(0f, -80f)
                quadToRelative(100f, 0f, 170f, -70f)
                reflectiveQuadToRelative(70f, -170f)
                quadToRelative(0f, -100f, -70f, -170f)
                reflectiveQuadToRelative(-170f, -70f)
                quadToRelative(-100f, 0f, -170f, 70f)
                reflectiveQuadToRelative(-70f, 170f)
                quadToRelative(0f, 100f, 70f, 170f)
                reflectiveQuadToRelative(170f, 70f)
                close()
            }
        }
    }.build()
}