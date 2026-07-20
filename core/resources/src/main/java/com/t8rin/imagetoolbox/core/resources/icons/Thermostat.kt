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

val Icons.Outlined.Thermostat: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Thermostat",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(560f, 440f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 400f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(560f, 360f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(720f, 400f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(680f, 440f)
            lineTo(560f, 440f)
            close()
            moveTo(560f, 280f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 240f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(560f, 200f)
            horizontalLineToRelative(240f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 240f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 280f)
            lineTo(560f, 280f)
            close()
            moveTo(178.5f, 781.5f)
            quadTo(120f, 723f, 120f, 640f)
            quadToRelative(0f, -48f, 21f, -89.5f)
            reflectiveQuadToRelative(59f, -70.5f)
            verticalLineToRelative(-240f)
            quadToRelative(0f, -50f, 35f, -85f)
            reflectiveQuadToRelative(85f, -35f)
            quadToRelative(50f, 0f, 85f, 35f)
            reflectiveQuadToRelative(35f, 85f)
            verticalLineToRelative(240f)
            quadToRelative(38f, 29f, 59f, 70.5f)
            reflectiveQuadToRelative(21f, 89.5f)
            quadToRelative(0f, 83f, -58.5f, 141.5f)
            reflectiveQuadTo(320f, 840f)
            quadToRelative(-83f, 0f, -141.5f, -58.5f)
            close()
            moveTo(200f, 640f)
            horizontalLineToRelative(240f)
            quadToRelative(0f, -29f, -12.5f, -54f)
            reflectiveQuadTo(392f, 544f)
            lineToRelative(-32f, -24f)
            verticalLineToRelative(-280f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(320f, 200f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(280f, 240f)
            verticalLineToRelative(280f)
            lineToRelative(-32f, 24f)
            quadToRelative(-23f, 17f, -35.5f, 42f)
            reflectiveQuadTo(200f, 640f)
            close()
        }
    }.build()
}
