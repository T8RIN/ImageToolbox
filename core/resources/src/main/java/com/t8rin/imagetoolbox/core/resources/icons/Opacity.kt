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

val Icons.Rounded.Opacity: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Opacity",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 840f)
            quadToRelative(-133f, 0f, -226.5f, -92f)
            reflectiveQuadTo(160f, 524f)
            quadToRelative(0f, -65f, 25f, -121.5f)
            reflectiveQuadTo(254f, 302f)
            lineToRelative(184f, -181f)
            quadToRelative(9f, -8f, 20f, -12.5f)
            reflectiveQuadToRelative(22f, -4.5f)
            quadToRelative(11f, 0f, 22f, 4.5f)
            reflectiveQuadToRelative(20f, 12.5f)
            lineToRelative(184f, 181f)
            quadToRelative(44f, 44f, 69f, 100.5f)
            reflectiveQuadTo(800f, 524f)
            quadToRelative(0f, 132f, -93.5f, 224f)
            reflectiveQuadTo(480f, 840f)
            close()
            moveTo(242f, 560f)
            horizontalLineToRelative(474f)
            quadToRelative(12f, -72f, -13.5f, -123f)
            reflectiveQuadTo(650f, 360f)
            lineTo(480f, 192f)
            lineTo(310f, 360f)
            quadToRelative(-27f, 26f, -53f, 77f)
            reflectiveQuadToRelative(-15f, 123f)
            close()
        }
    }.build()
}
