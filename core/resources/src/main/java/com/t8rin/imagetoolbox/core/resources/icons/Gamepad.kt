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

val Icons.Outlined.Gamepad: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Gamepad",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 306f)
            close()
            moveTo(654f, 480f)
            close()
            moveTo(306f, 480f)
            close()
            moveTo(480f, 654f)
            close()
            moveTo(452f, 392f)
            lineTo(372f, 312f)
            quadToRelative(-6f, -6f, -9f, -13.5f)
            reflectiveQuadToRelative(-3f, -15.5f)
            verticalLineToRelative(-163f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(400f, 80f)
            horizontalLineToRelative(160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(600f, 120f)
            verticalLineToRelative(163f)
            quadToRelative(0f, 8f, -3f, 15.5f)
            reflectiveQuadToRelative(-9f, 13.5f)
            lineToRelative(-80f, 80f)
            quadToRelative(-6f, 6f, -13f, 8.5f)
            reflectiveQuadToRelative(-15f, 2.5f)
            quadToRelative(-8f, 0f, -15f, -2.5f)
            reflectiveQuadToRelative(-13f, -8.5f)
            close()
            moveTo(568f, 508f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadToRelative(-2.5f, -15f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(80f, -80f)
            quadToRelative(6f, -6f, 13.5f, -9f)
            reflectiveQuadToRelative(15.5f, -3f)
            horizontalLineToRelative(163f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 400f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 600f)
            lineTo(677f, 600f)
            quadToRelative(-8f, 0f, -15.5f, -3f)
            reflectiveQuadToRelative(-13.5f, -9f)
            lineToRelative(-80f, -80f)
            close()
            moveTo(80f, 560f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 360f)
            horizontalLineToRelative(163f)
            quadToRelative(8f, 0f, 15.5f, 3f)
            reflectiveQuadToRelative(13.5f, 9f)
            lineToRelative(80f, 80f)
            quadToRelative(6f, 6f, 8.5f, 13f)
            reflectiveQuadToRelative(2.5f, 15f)
            quadToRelative(0f, 8f, -2.5f, 15f)
            reflectiveQuadToRelative(-8.5f, 13f)
            lineToRelative(-80f, 80f)
            quadToRelative(-6f, 6f, -13.5f, 9f)
            reflectiveQuadToRelative(-15.5f, 3f)
            lineTo(120f, 600f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(80f, 560f)
            close()
            moveTo(360f, 840f)
            verticalLineToRelative(-163f)
            quadToRelative(0f, -8f, 3f, -15.5f)
            reflectiveQuadToRelative(9f, -13.5f)
            lineToRelative(80f, -80f)
            quadToRelative(6f, -6f, 13f, -8.5f)
            reflectiveQuadToRelative(15f, -2.5f)
            quadToRelative(8f, 0f, 15f, 2.5f)
            reflectiveQuadToRelative(13f, 8.5f)
            lineToRelative(80f, 80f)
            quadToRelative(6f, 6f, 9f, 13.5f)
            reflectiveQuadToRelative(3f, 15.5f)
            verticalLineToRelative(163f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(560f, 880f)
            lineTo(400f, 880f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(360f, 840f)
            close()
            moveTo(480f, 306f)
            lineTo(520f, 266f)
            verticalLineToRelative(-106f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(106f)
            lineToRelative(40f, 40f)
            close()
            moveTo(160f, 520f)
            horizontalLineToRelative(106f)
            lineToRelative(40f, -40f)
            lineToRelative(-40f, -40f)
            lineTo(160f, 440f)
            verticalLineToRelative(80f)
            close()
            moveTo(440f, 800f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-106f)
            lineToRelative(-40f, -40f)
            lineToRelative(-40f, 40f)
            verticalLineToRelative(106f)
            close()
            moveTo(694f, 520f)
            horizontalLineToRelative(106f)
            verticalLineToRelative(-80f)
            lineTo(694f, 440f)
            lineToRelative(-40f, 40f)
            lineToRelative(40f, 40f)
            close()
        }
    }.build()
}
