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

val Icons.Outlined.Archive: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Archive",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(451.5f, 411.5f)
            quadTo(440f, 423f, 440f, 440f)
            verticalLineToRelative(128f)
            lineToRelative(-36f, -36f)
            quadToRelative(-11f, -11f, -28f, -11f)
            reflectiveQuadToRelative(-28f, 11f)
            quadToRelative(-11f, 11f, -11f, 28f)
            reflectiveQuadToRelative(11f, 28f)
            lineToRelative(104f, 104f)
            quadToRelative(12f, 12f, 28f, 12f)
            reflectiveQuadToRelative(28f, -12f)
            lineToRelative(104f, -104f)
            quadToRelative(11f, -11f, 11f, -28f)
            reflectiveQuadToRelative(-11f, -28f)
            quadToRelative(-11f, -11f, -28f, -11f)
            reflectiveQuadToRelative(-28f, 11f)
            lineToRelative(-36f, 36f)
            verticalLineToRelative(-128f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(480f, 400f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            close()
            moveTo(200f, 320f)
            verticalLineToRelative(440f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-440f)
            lineTo(200f, 320f)
            close()
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-499f)
            quadToRelative(0f, -14f, 4.5f, -27f)
            reflectiveQuadToRelative(13.5f, -24f)
            lineToRelative(50f, -61f)
            quadToRelative(11f, -14f, 27.5f, -21.5f)
            reflectiveQuadTo(250f, 120f)
            horizontalLineToRelative(460f)
            quadToRelative(18f, 0f, 34.5f, 7.5f)
            reflectiveQuadTo(772f, 149f)
            lineToRelative(50f, 61f)
            quadToRelative(9f, 11f, 13.5f, 24f)
            reflectiveQuadToRelative(4.5f, 27f)
            verticalLineToRelative(499f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(216f, 240f)
            horizontalLineToRelative(528f)
            lineToRelative(-34f, -40f)
            lineTo(250f, 200f)
            lineToRelative(-34f, 40f)
            close()
            moveTo(480f, 540f)
            close()
        }
    }.build()
}
