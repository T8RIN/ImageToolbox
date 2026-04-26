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

val Icons.Outlined.Call: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Call",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(798f, 840f)
            quadToRelative(-125f, 0f, -247f, -54.5f)
            reflectiveQuadTo(329f, 631f)
            quadTo(229f, 531f, 174.5f, 409f)
            reflectiveQuadTo(120f, 162f)
            quadToRelative(0f, -18f, 12f, -30f)
            reflectiveQuadToRelative(30f, -12f)
            horizontalLineToRelative(162f)
            quadToRelative(14f, 0f, 25f, 9.5f)
            reflectiveQuadToRelative(13f, 22.5f)
            lineToRelative(26f, 140f)
            quadToRelative(2f, 16f, -1f, 27f)
            reflectiveQuadToRelative(-11f, 19f)
            lineToRelative(-97f, 98f)
            quadToRelative(20f, 37f, 47.5f, 71.5f)
            reflectiveQuadTo(387f, 574f)
            quadToRelative(31f, 31f, 65f, 57.5f)
            reflectiveQuadToRelative(72f, 48.5f)
            lineToRelative(94f, -94f)
            quadToRelative(9f, -9f, 23.5f, -13.5f)
            reflectiveQuadTo(670f, 570f)
            lineToRelative(138f, 28f)
            quadToRelative(14f, 4f, 23f, 14.5f)
            reflectiveQuadToRelative(9f, 23.5f)
            verticalLineToRelative(162f)
            quadToRelative(0f, 18f, -12f, 30f)
            reflectiveQuadToRelative(-30f, 12f)
            close()
            moveTo(241f, 360f)
            lineToRelative(66f, -66f)
            lineToRelative(-17f, -94f)
            horizontalLineToRelative(-89f)
            quadToRelative(5f, 41f, 14f, 81f)
            reflectiveQuadToRelative(26f, 79f)
            close()
            moveTo(599f, 718f)
            quadToRelative(39f, 17f, 79.5f, 27f)
            reflectiveQuadToRelative(81.5f, 13f)
            verticalLineToRelative(-88f)
            lineToRelative(-94f, -19f)
            lineToRelative(-67f, 67f)
            close()
            moveTo(241f, 360f)
            close()
            moveTo(599f, 718f)
            close()
        }
    }.build()
}
