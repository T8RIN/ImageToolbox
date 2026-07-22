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

val Icons.Outlined.TiltArrowDown: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.TiltArrowDown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(240f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(160f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(240f, 120f)
            horizontalLineToRelative(480f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(800f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(720f, 840f)
            lineTo(240f, 840f)
            close()
            moveTo(240f, 760f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-560f)
            lineTo(240f, 200f)
            verticalLineToRelative(560f)
            close()
            moveTo(440f, 487f)
            lineTo(404f, 451f)
            quadToRelative(-11f, -11f, -27.5f, -11f)
            reflectiveQuadTo(348f, 452f)
            quadToRelative(-11f, 11f, -11f, 28f)
            reflectiveQuadToRelative(11f, 28f)
            lineToRelative(104f, 104f)
            quadToRelative(12f, 12f, 28f, 12f)
            reflectiveQuadToRelative(28f, -12f)
            lineToRelative(104f, -104f)
            quadToRelative(11f, -11f, 11f, -27.5f)
            reflectiveQuadTo(612f, 452f)
            quadToRelative(-12f, -12f, -28.5f, -12f)
            reflectiveQuadTo(555f, 452f)
            lineToRelative(-35f, 35f)
            verticalLineToRelative(-127f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(480f, 320f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(440f, 360f)
            verticalLineToRelative(127f)
            close()
            moveTo(720f, 760f)
            lineTo(240f, 760f)
            horizontalLineToRelative(480f)
            close()
        }
    }.build()
}
