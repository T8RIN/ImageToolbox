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

val Icons.Outlined.DarkMode: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.DarkMode",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 840f)
            quadToRelative(-151f, 0f, -255.5f, -104.5f)
            reflectiveQuadTo(120f, 480f)
            quadToRelative(0f, -138f, 90f, -239.5f)
            reflectiveQuadTo(440f, 122f)
            quadToRelative(13f, -2f, 23f, 3.5f)
            reflectiveQuadToRelative(16f, 14.5f)
            quadToRelative(6f, 9f, 6.5f, 21f)
            reflectiveQuadToRelative(-7.5f, 23f)
            quadToRelative(-17f, 26f, -25.5f, 55f)
            reflectiveQuadToRelative(-8.5f, 61f)
            quadToRelative(0f, 90f, 63f, 153f)
            reflectiveQuadToRelative(153f, 63f)
            quadToRelative(31f, 0f, 61.5f, -9f)
            reflectiveQuadToRelative(54.5f, -25f)
            quadToRelative(11f, -7f, 22.5f, -6.5f)
            reflectiveQuadTo(819f, 481f)
            quadToRelative(10f, 5f, 15.5f, 15f)
            reflectiveQuadToRelative(3.5f, 24f)
            quadToRelative(-14f, 138f, -117.5f, 229f)
            reflectiveQuadTo(480f, 840f)
            close()
            moveTo(480f, 760f)
            quadToRelative(88f, 0f, 158f, -48.5f)
            reflectiveQuadTo(740f, 585f)
            quadToRelative(-20f, 5f, -40f, 8f)
            reflectiveQuadToRelative(-40f, 3f)
            quadToRelative(-123f, 0f, -209.5f, -86.5f)
            reflectiveQuadTo(364f, 300f)
            quadToRelative(0f, -20f, 3f, -40f)
            reflectiveQuadToRelative(8f, -40f)
            quadToRelative(-78f, 32f, -126.5f, 102f)
            reflectiveQuadTo(200f, 480f)
            quadToRelative(0f, 116f, 82f, 198f)
            reflectiveQuadToRelative(198f, 82f)
            close()
            moveTo(470f, 490f)
            close()
        }
    }.build()
}

val Icons.Rounded.DarkMode: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.DarkMode",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 840f)
            quadToRelative(-151f, 0f, -255.5f, -104.5f)
            reflectiveQuadTo(120f, 480f)
            quadToRelative(0f, -138f, 90f, -239.5f)
            reflectiveQuadTo(440f, 122f)
            quadToRelative(13f, -2f, 23f, 3.5f)
            reflectiveQuadToRelative(16f, 14.5f)
            quadToRelative(6f, 9f, 6.5f, 21f)
            reflectiveQuadToRelative(-7.5f, 23f)
            quadToRelative(-17f, 26f, -25.5f, 55f)
            reflectiveQuadToRelative(-8.5f, 61f)
            quadToRelative(0f, 90f, 63f, 153f)
            reflectiveQuadToRelative(153f, 63f)
            quadToRelative(31f, 0f, 61.5f, -9f)
            reflectiveQuadToRelative(54.5f, -25f)
            quadToRelative(11f, -7f, 22.5f, -6.5f)
            reflectiveQuadTo(819f, 481f)
            quadToRelative(10f, 5f, 15.5f, 15f)
            reflectiveQuadToRelative(3.5f, 24f)
            quadToRelative(-14f, 138f, -117.5f, 229f)
            reflectiveQuadTo(480f, 840f)
            close()
        }
    }.build()
}
