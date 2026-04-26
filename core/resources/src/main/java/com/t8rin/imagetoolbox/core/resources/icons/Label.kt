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

val Icons.Rounded.Label: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Label",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(807f, 526f)
            lineTo(666f, 726f)
            quadToRelative(-11f, 16f, -28.5f, 25f)
            reflectiveQuadToRelative(-37.5f, 9f)
            lineTo(200f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 680f)
            verticalLineToRelative(-400f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 200f)
            horizontalLineToRelative(400f)
            quadToRelative(20f, 0f, 37.5f, 9f)
            reflectiveQuadToRelative(28.5f, 25f)
            lineToRelative(141f, 200f)
            quadToRelative(15f, 21f, 15f, 46f)
            reflectiveQuadToRelative(-15f, 46f)
            close()
        }
    }.build()
}
