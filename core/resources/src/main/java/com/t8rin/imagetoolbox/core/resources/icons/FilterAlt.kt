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

val Icons.Rounded.FilterAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FilterAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(440f, 800f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(400f, 760f)
            verticalLineToRelative(-240f)
            lineTo(168f, 224f)
            quadToRelative(-15f, -20f, -4.5f, -42f)
            reflectiveQuadToRelative(36.5f, -22f)
            horizontalLineToRelative(560f)
            quadToRelative(26f, 0f, 36.5f, 22f)
            reflectiveQuadToRelative(-4.5f, 42f)
            lineTo(560f, 520f)
            verticalLineToRelative(240f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(520f, 800f)
            horizontalLineToRelative(-80f)
            close()
        }
    }.build()
}
