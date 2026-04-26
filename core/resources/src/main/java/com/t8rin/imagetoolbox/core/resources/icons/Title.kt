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

val Icons.Rounded.Title: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Title",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(420f, 280f)
            lineTo(260f, 280f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(200f, 220f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(260f, 160f)
            horizontalLineToRelative(440f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(760f, 220f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(700f, 280f)
            lineTo(540f, 280f)
            verticalLineToRelative(460f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(480f, 800f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(420f, 740f)
            verticalLineToRelative(-460f)
            close()
        }
    }.build()
}
