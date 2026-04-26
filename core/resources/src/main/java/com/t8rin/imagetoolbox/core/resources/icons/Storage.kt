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

val Icons.Rounded.Storage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Storage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(200f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 720f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 640f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 720f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 800f)
            lineTo(200f, 800f)
            close()
            moveTo(200f, 320f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 240f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 160f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 240f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 320f)
            lineTo(200f, 320f)
            close()
            moveTo(200f, 560f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 400f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 480f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 560f)
            lineTo(200f, 560f)
            close()
            moveTo(240f, 280f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(280f, 240f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(240f, 200f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(200f, 240f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(240f, 280f)
            close()
            moveTo(240f, 520f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(280f, 480f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(240f, 440f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(200f, 480f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(240f, 520f)
            close()
            moveTo(240f, 760f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(280f, 720f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(240f, 680f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(200f, 720f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(240f, 760f)
            close()
        }
    }.build()
}
