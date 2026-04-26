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

val Icons.Rounded.Contacts: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Contacts",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 520f)
            quadToRelative(50f, 0f, 85f, -35f)
            reflectiveQuadToRelative(35f, -85f)
            quadToRelative(0f, -50f, -35f, -85f)
            reflectiveQuadToRelative(-85f, -35f)
            quadToRelative(-50f, 0f, -85f, 35f)
            reflectiveQuadToRelative(-35f, 85f)
            quadToRelative(0f, 50f, 35f, 85f)
            reflectiveQuadToRelative(85f, 35f)
            close()
            moveTo(160f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(640f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 240f)
            verticalLineToRelative(480f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 800f)
            lineTo(160f, 800f)
            close()
            moveTo(230f, 720f)
            horizontalLineToRelative(500f)
            quadToRelative(-45f, -56f, -109f, -88f)
            reflectiveQuadToRelative(-141f, -32f)
            quadToRelative(-77f, 0f, -141f, 32f)
            reflectiveQuadToRelative(-109f, 88f)
            close()
            moveTo(200f, 920f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 880f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(200f, 840f)
            horizontalLineToRelative(560f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(800f, 880f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(760f, 920f)
            lineTo(200f, 920f)
            close()
            moveTo(200f, 120f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(160f, 80f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(200f, 40f)
            horizontalLineToRelative(560f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(800f, 80f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(760f, 120f)
            lineTo(200f, 120f)
            close()
        }
    }.build()
}
