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

val Icons.Rounded.RampLeft: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.RampLeft",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(440f, 800f)
            verticalLineToRelative(-527f)
            lineToRelative(-36f, 36f)
            quadToRelative(-11f, 11f, -27.5f, 11f)
            reflectiveQuadTo(348f, 308f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            lineToRelative(104f, -104f)
            quadToRelative(6f, -6f, 13f, -8.5f)
            reflectiveQuadToRelative(15f, -2.5f)
            quadToRelative(8f, 0f, 15f, 2.5f)
            reflectiveQuadToRelative(13f, 8.5f)
            lineToRelative(104f, 104f)
            quadToRelative(11f, 11f, 11.5f, 27.5f)
            reflectiveQuadTo(612f, 308f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineToRelative(-36f, -35f)
            verticalLineToRelative(87f)
            quadToRelative(0f, 109f, 68.5f, 186.5f)
            reflectiveQuadTo(720f, 666f)
            quadToRelative(15f, 10f, 18.5f, 26.5f)
            reflectiveQuadTo(731f, 720f)
            quadToRelative(-14f, 14f, -32.5f, 15f)
            reflectiveQuadTo(664f, 725f)
            quadToRelative(-45f, -31f, -80.5f, -65f)
            reflectiveQuadTo(520f, 588f)
            verticalLineToRelative(212f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 840f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(440f, 800f)
            close()
        }
    }.build()
}
