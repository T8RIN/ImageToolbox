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

val Icons.Rounded.HourglassEmpty: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.HourglassEmpty",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(320f, 800f)
            horizontalLineToRelative(320f)
            verticalLineToRelative(-120f)
            quadToRelative(0f, -66f, -47f, -113f)
            reflectiveQuadToRelative(-113f, -47f)
            quadToRelative(-66f, 0f, -113f, 47f)
            reflectiveQuadToRelative(-47f, 113f)
            verticalLineToRelative(120f)
            close()
            moveTo(480f, 440f)
            quadToRelative(66f, 0f, 113f, -47f)
            reflectiveQuadToRelative(47f, -113f)
            verticalLineToRelative(-120f)
            lineTo(320f, 160f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 66f, 47f, 113f)
            reflectiveQuadToRelative(113f, 47f)
            close()
            moveTo(200f, 880f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 840f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(200f, 800f)
            horizontalLineToRelative(40f)
            verticalLineToRelative(-120f)
            quadToRelative(0f, -61f, 28.5f, -114.5f)
            reflectiveQuadTo(348f, 480f)
            quadToRelative(-51f, -32f, -79.5f, -85.5f)
            reflectiveQuadTo(240f, 280f)
            verticalLineToRelative(-120f)
            horizontalLineToRelative(-40f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 120f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(200f, 80f)
            horizontalLineToRelative(560f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(800f, 120f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(760f, 160f)
            horizontalLineToRelative(-40f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 61f, -28.5f, 114.5f)
            reflectiveQuadTo(612f, 480f)
            quadToRelative(51f, 32f, 79.5f, 85.5f)
            reflectiveQuadTo(720f, 680f)
            verticalLineToRelative(120f)
            horizontalLineToRelative(40f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(800f, 840f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(760f, 880f)
            lineTo(200f, 880f)
            close()
        }
    }.build()
}
