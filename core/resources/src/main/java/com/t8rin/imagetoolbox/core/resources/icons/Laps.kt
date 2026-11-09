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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.Laps: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Laps",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(80f, 440f)
            quadToRelative(0f, -117f, 81.5f, -198.5f)
            reflectiveQuadTo(360f, 160f)
            horizontalLineToRelative(240f)
            quadToRelative(117f, 0f, 198.5f, 81.5f)
            reflectiveQuadTo(880f, 440f)
            quadToRelative(0f, 103f, -66f, 181f)
            reflectiveQuadToRelative(-168f, 95f)
            quadToRelative(-18f, 3f, -32f, -8f)
            reflectiveQuadToRelative(-14f, -28f)
            quadToRelative(0f, -17f, 13.5f, -29f)
            reflectiveQuadToRelative(31.5f, -16f)
            quadToRelative(69f, -16f, 112f, -70.5f)
            reflectiveQuadTo(800f, 440f)
            quadToRelative(0f, -83f, -58.5f, -141.5f)
            reflectiveQuadTo(600f, 240f)
            lineTo(360f, 240f)
            quadToRelative(-83f, 0f, -141.5f, 58.5f)
            reflectiveQuadTo(160f, 440f)
            quadToRelative(0f, 83f, 58.5f, 141.5f)
            reflectiveQuadTo(360f, 640f)
            horizontalLineToRelative(7f)
            lineToRelative(-36f, -36f)
            quadToRelative(-11f, -11f, -11f, -27.5f)
            reflectiveQuadToRelative(12f, -28.5f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            lineToRelative(104f, 104f)
            quadToRelative(12f, 12f, 12f, 28f)
            reflectiveQuadToRelative(-12f, 28f)
            lineTo(388f, 812f)
            quadToRelative(-11f, 11f, -27.5f, 11f)
            reflectiveQuadTo(332f, 812f)
            quadToRelative(-12f, -12f, -12f, -28.5f)
            reflectiveQuadToRelative(12f, -28.5f)
            lineToRelative(35f, -35f)
            horizontalLineToRelative(-7f)
            quadToRelative(-117f, 0f, -198.5f, -81.5f)
            reflectiveQuadTo(80f, 440f)
            close()
        }
    }.build()
}
