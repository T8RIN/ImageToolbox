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

val Icons.Rounded.AreaChart: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.AreaChart",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(840f, 640f)
            lineTo(529f, 397f)
            quadToRelative(-27f, -21f, -60.5f, -16.5f)
            reflectiveQuadTo(415f, 413f)
            lineToRelative(-86f, 118f)
            quadToRelative(-10f, 14f, -26.5f, 16.5f)
            reflectiveQuadTo(272f, 539f)
            lineTo(120f, 420f)
            verticalLineToRelative(-60f)
            quadToRelative(0f, -25f, 22f, -36f)
            reflectiveQuadToRelative(42f, 4f)
            lineToRelative(96f, 72f)
            lineToRelative(151f, -211f)
            quadToRelative(20f, -28f, 54f, -33f)
            reflectiveQuadToRelative(61f, 17f)
            lineToRelative(134f, 107f)
            horizontalLineToRelative(80f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 360f)
            verticalLineToRelative(280f)
            close()
            moveTo(120f, 800f)
            verticalLineToRelative(-280f)
            lineToRelative(135f, 108f)
            quadToRelative(27f, 22f, 60.5f, 17f)
            reflectiveQuadToRelative(53.5f, -33f)
            lineToRelative(87f, -119f)
            quadToRelative(10f, -14f, 26.5f, -16.5f)
            reflectiveQuadTo(513f, 485f)
            lineToRelative(327f, 256f)
            verticalLineToRelative(59f)
            lineTo(120f, 800f)
            close()
        }
    }.build()
}

val Icons.Outlined.AreaChart: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.AreaChart",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(546f, 173f)
            lineToRelative(134f, 107f)
            horizontalLineToRelative(80f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 360f)
            verticalLineToRelative(440f)
            lineTo(120f, 800f)
            verticalLineToRelative(-440f)
            quadToRelative(0f, -25f, 22f, -36f)
            reflectiveQuadToRelative(42f, 4f)
            lineToRelative(96f, 72f)
            lineToRelative(151f, -211f)
            quadToRelative(20f, -28f, 54f, -33f)
            reflectiveQuadToRelative(61f, 17f)
            close()
            moveTo(200f, 440f)
            verticalLineToRelative(144f)
            lineToRelative(120f, 96f)
            lineToRelative(160f, -220f)
            lineToRelative(280f, 218f)
            verticalLineToRelative(-318f)
            lineTo(652f, 360f)
            lineTo(496f, 235f)
            lineTo(298f, 513f)
            lineToRelative(-98f, -73f)
            close()
        }
    }.build()
}