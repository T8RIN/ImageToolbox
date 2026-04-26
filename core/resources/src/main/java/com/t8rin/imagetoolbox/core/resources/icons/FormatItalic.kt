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

val Icons.Rounded.FormatItalic: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FormatItalic",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(250f, 760f)
            quadToRelative(-21f, 0f, -35.5f, -14.5f)
            reflectiveQuadTo(200f, 710f)
            quadToRelative(0f, -21f, 14.5f, -35.5f)
            reflectiveQuadTo(250f, 660f)
            horizontalLineToRelative(110f)
            lineToRelative(120f, -360f)
            lineTo(370f, 300f)
            quadToRelative(-21f, 0f, -35.5f, -14.5f)
            reflectiveQuadTo(320f, 250f)
            quadToRelative(0f, -21f, 14.5f, -35.5f)
            reflectiveQuadTo(370f, 200f)
            horizontalLineToRelative(300f)
            quadToRelative(21f, 0f, 35.5f, 14.5f)
            reflectiveQuadTo(720f, 250f)
            quadToRelative(0f, 21f, -14.5f, 35.5f)
            reflectiveQuadTo(670f, 300f)
            horizontalLineToRelative(-90f)
            lineTo(460f, 660f)
            horizontalLineToRelative(90f)
            quadToRelative(21f, 0f, 35.5f, 14.5f)
            reflectiveQuadTo(600f, 710f)
            quadToRelative(0f, 21f, -14.5f, 35.5f)
            reflectiveQuadTo(550f, 760f)
            lineTo(250f, 760f)
            close()
        }
    }.build()
}
