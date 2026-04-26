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

val Icons.Outlined.WallpaperAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Wallpaper",
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
            moveTo(320f, 600f)
            horizontalLineToRelative(320f)
            lineTo(535f, 460f)
            lineToRelative(-75f, 100f)
            lineToRelative(-55f, -73f)
            lineToRelative(-85f, 113f)
            close()
            moveTo(600f, 400f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(640f, 360f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(600f, 320f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(560f, 360f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(600f, 400f)
            close()
            moveTo(280f, 840f)
            verticalLineToRelative(-720f)
            verticalLineToRelative(720f)
            close()
        }
    }.build()
}
