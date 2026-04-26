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

val Icons.Outlined.Flag: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Flag",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(280f, 560f)
            verticalLineToRelative(240f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(240f, 840f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(200f, 800f)
            verticalLineToRelative(-600f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(240f, 160f)
            horizontalLineToRelative(287f)
            quadToRelative(14f, 0f, 25f, 9f)
            reflectiveQuadToRelative(14f, 23f)
            lineToRelative(10f, 48f)
            horizontalLineToRelative(184f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(800f, 280f)
            verticalLineToRelative(320f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(760f, 640f)
            lineTo(553f, 640f)
            quadToRelative(-14f, 0f, -25f, -9f)
            reflectiveQuadToRelative(-14f, -23f)
            lineToRelative(-10f, -48f)
            lineTo(280f, 560f)
            close()
            moveTo(586f, 560f)
            horizontalLineToRelative(134f)
            verticalLineToRelative(-240f)
            lineTo(543f, 320f)
            quadToRelative(-14f, 0f, -25f, -9f)
            reflectiveQuadToRelative(-14f, -23f)
            lineToRelative(-10f, -48f)
            lineTo(280f, 240f)
            verticalLineToRelative(240f)
            horizontalLineToRelative(257f)
            quadToRelative(14f, 0f, 25f, 9f)
            reflectiveQuadToRelative(14f, 23f)
            lineToRelative(10f, 48f)
            close()
            moveTo(500f, 400f)
            close()
        }
    }.build()
}
