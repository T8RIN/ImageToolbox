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

val Icons.Rounded.TimerOff: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.TimerOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 880f)
            quadToRelative(-74f, 0f, -139.5f, -28.5f)
            reflectiveQuadTo(226f, 774f)
            quadToRelative(-49f, -49f, -77.5f, -114.5f)
            reflectiveQuadTo(120f, 520f)
            quadToRelative(0f, -60f, 18.5f, -115.5f)
            reflectiveQuadTo(192f, 304f)
            lineTo(84f, 196f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            lineToRelative(680f, 680f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineToRelative(-68f, -68f)
            quadToRelative(-48f, 35f, -103.5f, 53.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(400f, 120f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(360f, 80f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(400f, 40f)
            horizontalLineToRelative(160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(600f, 80f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(560f, 120f)
            lineTo(400f, 120f)
            close()
            moveTo(748f, 636f)
            lineTo(520f, 408f)
            verticalLineToRelative(-48f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(480f, 320f)
            quadToRelative(-10f, 0f, -18.5f, 4f)
            reflectiveQuadTo(448f, 336f)
            lineToRelative(-85f, -85f)
            quadToRelative(-18f, -18f, -13f, -43f)
            reflectiveQuadToRelative(29f, -33f)
            quadToRelative(24f, -8f, 49.5f, -11.5f)
            reflectiveQuadTo(480f, 160f)
            quadToRelative(60f, 0f, 117.5f, 20f)
            reflectiveQuadTo(706f, 238f)
            lineToRelative(28f, -28f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            lineToRelative(-28f, 28f)
            quadToRelative(38f, 51f, 58f, 108.5f)
            reflectiveQuadTo(840f, 520f)
            quadToRelative(0f, 26f, -3.5f, 51f)
            reflectiveQuadTo(825f, 621f)
            quadToRelative(-8f, 24f, -33f, 29f)
            reflectiveQuadToRelative(-44f, -14f)
            close()
        }
    }.build()
}