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

val Icons.Rounded.HorizontalSplit: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.HorizontalSplit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 760f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(120f, 720f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(160f, 520f)
            horizontalLineToRelative(640f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 560f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 760f)
            lineTo(160f, 760f)
            close()
            moveTo(160f, 440f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(120f, 400f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(160f, 360f)
            horizontalLineToRelative(640f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 400f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 440f)
            lineTo(160f, 440f)
            close()
            moveTo(160f, 280f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(120f, 240f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(160f, 200f)
            horizontalLineToRelative(640f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 240f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 280f)
            lineTo(160f, 280f)
            close()
        }
    }.build()
}
