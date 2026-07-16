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

val Icons.Outlined.DataThresholding: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.DataThresholding",
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
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(691f, 760f)
            horizontalLineToRelative(69f)
            verticalLineToRelative(-69f)
            lineToRelative(-69f, 69f)
            close()
            moveTo(234f, 760f)
            horizontalLineToRelative(73f)
            lineToRelative(120f, -120f)
            horizontalLineToRelative(85f)
            lineTo(392f, 760f)
            horizontalLineToRelative(64f)
            lineToRelative(120f, -120f)
            horizontalLineToRelative(85f)
            lineTo(541f, 760f)
            horizontalLineToRelative(65f)
            lineToRelative(120f, -120f)
            horizontalLineToRelative(34f)
            verticalLineToRelative(-440f)
            lineTo(200f, 200f)
            verticalLineToRelative(509f)
            lineToRelative(69f, -69f)
            horizontalLineToRelative(85f)
            lineTo(234f, 760f)
            close()
            moveTo(427f, 440f)
            lineTo(334f, 532f)
            quadToRelative(-11f, 11f, -27.5f, 11.5f)
            reflectiveQuadTo(278f, 532f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            lineToRelative(121f, -121f)
            quadToRelative(12f, -12f, 28f, -12f)
            reflectiveQuadToRelative(28f, 12f)
            lineToRelative(52f, 52f)
            lineToRelative(119f, -119f)
            quadToRelative(11f, -11f, 27.5f, -11.5f)
            reflectiveQuadTo(682f, 288f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            lineTo(535f, 492f)
            quadToRelative(-12f, 12f, -28f, 12f)
            reflectiveQuadToRelative(-28f, -12f)
            lineToRelative(-52f, -52f)
            close()
            moveTo(200f, 760f)
            verticalLineToRelative(-560f)
            verticalLineToRelative(560f)
            close()
        }
    }.build()
}
