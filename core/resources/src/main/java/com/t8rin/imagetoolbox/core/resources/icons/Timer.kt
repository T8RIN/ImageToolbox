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

val Icons.Outlined.Timer: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Timer",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
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
            moveTo(480f, 560f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(520f, 520f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(480f, 320f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(440f, 360f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(480f, 560f)
            close()
            moveTo(480f, 880f)
            quadToRelative(-74f, 0f, -139.5f, -28.5f)
            reflectiveQuadTo(226f, 774f)
            quadToRelative(-49f, -49f, -77.5f, -114.5f)
            reflectiveQuadTo(120f, 520f)
            quadToRelative(0f, -74f, 28.5f, -139.5f)
            reflectiveQuadTo(226f, 266f)
            quadToRelative(49f, -49f, 114.5f, -77.5f)
            reflectiveQuadTo(480f, 160f)
            quadToRelative(62f, 0f, 119f, 20f)
            reflectiveQuadToRelative(107f, 58f)
            lineToRelative(28f, -28f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            lineToRelative(-28f, 28f)
            quadToRelative(38f, 50f, 58f, 107f)
            reflectiveQuadToRelative(20f, 119f)
            quadToRelative(0f, 74f, -28.5f, 139.5f)
            reflectiveQuadTo(734f, 774f)
            quadToRelative(-49f, 49f, -114.5f, 77.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(480f, 800f)
            quadToRelative(116f, 0f, 198f, -82f)
            reflectiveQuadToRelative(82f, -198f)
            quadToRelative(0f, -116f, -82f, -198f)
            reflectiveQuadToRelative(-198f, -82f)
            quadToRelative(-116f, 0f, -198f, 82f)
            reflectiveQuadToRelative(-82f, 198f)
            quadToRelative(0f, 116f, 82f, 198f)
            reflectiveQuadToRelative(198f, 82f)
            close()
            moveTo(480f, 520f)
            close()
        }
    }.build()
}
