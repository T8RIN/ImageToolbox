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

val Icons.Outlined.WallpaperAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.WallpaperAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(280f, 920f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(200f, 840f)
            verticalLineToRelative(-720f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 40f)
            horizontalLineToRelative(400f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(760f, 120f)
            verticalLineToRelative(124f)
            quadToRelative(18f, 7f, 29f, 22f)
            reflectiveQuadToRelative(11f, 34f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 19f, -11f, 34f)
            reflectiveQuadToRelative(-29f, 22f)
            verticalLineToRelative(404f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(680f, 920f)
            lineTo(280f, 920f)
            close()
            moveTo(280f, 840f)
            horizontalLineToRelative(400f)
            verticalLineToRelative(-720f)
            lineTo(280f, 120f)
            verticalLineToRelative(720f)
            close()
            moveTo(620f, 600f)
            quadToRelative(6f, 0f, 9f, -5.5f)
            reflectiveQuadToRelative(-1f, -10.5f)
            lineToRelative(-85f, -113f)
            quadToRelative(-3f, -4f, -8f, -4f)
            reflectiveQuadToRelative(-8f, 4f)
            lineToRelative(-67f, 89f)
            lineToRelative(-47f, -63f)
            quadToRelative(-3f, -4f, -8f, -4f)
            reflectiveQuadToRelative(-8f, 4f)
            lineToRelative(-65f, 87f)
            quadToRelative(-4f, 5f, -1f, 10.5f)
            reflectiveQuadToRelative(9f, 5.5f)
            horizontalLineToRelative(280f)
            close()
            moveTo(628.5f, 388.5f)
            quadTo(640f, 377f, 640f, 360f)
            reflectiveQuadToRelative(-11.5f, -28.5f)
            quadTo(617f, 320f, 600f, 320f)
            reflectiveQuadToRelative(-28.5f, 11.5f)
            quadTo(560f, 343f, 560f, 360f)
            reflectiveQuadToRelative(11.5f, 28.5f)
            quadTo(583f, 400f, 600f, 400f)
            reflectiveQuadToRelative(28.5f, -11.5f)
            close()
            moveTo(280f, 840f)
            verticalLineToRelative(-720f)
            verticalLineToRelative(720f)
            close()
        }
    }.build()
}