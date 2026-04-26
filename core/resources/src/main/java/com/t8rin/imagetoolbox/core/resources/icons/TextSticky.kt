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

val Icons.Outlined.TextSticky: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.TextSticky",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
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
            moveTo(440f, 400f)
            verticalLineToRelative(200f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(480f, 640f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(520f, 600f)
            verticalLineToRelative(-200f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(640f, 360f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(600f, 320f)
            lineTo(360f, 320f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(320f, 360f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(360f, 400f)
            horizontalLineToRelative(80f)
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
