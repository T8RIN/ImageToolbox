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

val Icons.Outlined.Preview: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Preview",
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
            moveTo(200f, 760f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-480f)
            lineTo(200f, 280f)
            verticalLineToRelative(480f)
            close()
            moveTo(333.5f, 635.5f)
            quadTo(269f, 591f, 240f, 520f)
            quadToRelative(29f, -71f, 93.5f, -115.5f)
            reflectiveQuadTo(480f, 360f)
            quadToRelative(82f, 0f, 146.5f, 44.5f)
            reflectiveQuadTo(720f, 520f)
            quadToRelative(-29f, 71f, -93.5f, 115.5f)
            reflectiveQuadTo(480f, 680f)
            quadToRelative(-82f, 0f, -146.5f, -44.5f)
            close()
            moveTo(582f, 593.5f)
            quadToRelative(46f, -26.5f, 72f, -73.5f)
            quadToRelative(-26f, -47f, -72f, -73.5f)
            reflectiveQuadTo(480f, 420f)
            quadToRelative(-56f, 0f, -102f, 26.5f)
            reflectiveQuadTo(306f, 520f)
            quadToRelative(26f, 47f, 72f, 73.5f)
            reflectiveQuadTo(480f, 620f)
            quadToRelative(56f, 0f, 102f, -26.5f)
            close()
            moveTo(480f, 520f)
            close()
            moveTo(522.5f, 562.5f)
            quadTo(540f, 545f, 540f, 520f)
            reflectiveQuadToRelative(-17.5f, -42.5f)
            quadTo(505f, 460f, 480f, 460f)
            reflectiveQuadToRelative(-42.5f, 17.5f)
            quadTo(420f, 495f, 420f, 520f)
            reflectiveQuadToRelative(17.5f, 42.5f)
            quadTo(455f, 580f, 480f, 580f)
            reflectiveQuadToRelative(42.5f, -17.5f)
            close()
        }
    }.build()
}
