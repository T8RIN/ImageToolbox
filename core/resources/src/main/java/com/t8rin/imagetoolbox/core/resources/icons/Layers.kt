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

val Icons.Outlined.Layers: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Layers",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(161f, 594f)
            quadToRelative(-16f, -12f, -15.5f, -31.5f)
            reflectiveQuadTo(162f, 531f)
            quadToRelative(11f, -8f, 24f, -8f)
            reflectiveQuadToRelative(24f, 8f)
            lineToRelative(270f, 209f)
            lineToRelative(270f, -209f)
            quadToRelative(11f, -8f, 24f, -8f)
            reflectiveQuadToRelative(24f, 8f)
            quadToRelative(16f, 12f, 16.5f, 31.5f)
            reflectiveQuadTo(799f, 594f)
            lineTo(529f, 804f)
            quadToRelative(-22f, 17f, -49f, 17f)
            reflectiveQuadToRelative(-49f, -17f)
            lineTo(161f, 594f)
            close()
            moveTo(431f, 602f)
            lineTo(201f, 423f)
            quadToRelative(-31f, -24f, -31f, -63f)
            reflectiveQuadToRelative(31f, -63f)
            lineToRelative(230f, -179f)
            quadToRelative(22f, -17f, 49f, -17f)
            reflectiveQuadToRelative(49f, 17f)
            lineToRelative(230f, 179f)
            quadToRelative(31f, 24f, 31f, 63f)
            reflectiveQuadToRelative(-31f, 63f)
            lineTo(529f, 602f)
            quadToRelative(-22f, 17f, -49f, 17f)
            reflectiveQuadToRelative(-49f, -17f)
            close()
            moveTo(480f, 538f)
            lineTo(710f, 360f)
            lineTo(480f, 182f)
            lineTo(250f, 360f)
            lineTo(480f, 538f)
            close()
            moveTo(480f, 360f)
            close()
        }
    }.build()
}

val Icons.Rounded.Layers: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Layers",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(161f, 594f)
            quadToRelative(-16f, -12f, -15.5f, -31.5f)
            reflectiveQuadTo(162f, 531f)
            quadToRelative(11f, -8f, 24f, -8f)
            reflectiveQuadToRelative(24f, 8f)
            lineToRelative(270f, 209f)
            lineToRelative(270f, -209f)
            quadToRelative(11f, -8f, 24f, -8f)
            reflectiveQuadToRelative(24f, 8f)
            quadToRelative(16f, 12f, 16.5f, 31.5f)
            reflectiveQuadTo(799f, 594f)
            lineTo(529f, 804f)
            quadToRelative(-22f, 17f, -49f, 17f)
            reflectiveQuadToRelative(-49f, -17f)
            lineTo(161f, 594f)
            close()
            moveTo(431f, 602f)
            lineTo(201f, 423f)
            quadToRelative(-31f, -24f, -31f, -63f)
            reflectiveQuadToRelative(31f, -63f)
            lineToRelative(230f, -179f)
            quadToRelative(22f, -17f, 49f, -17f)
            reflectiveQuadToRelative(49f, 17f)
            lineToRelative(230f, 179f)
            quadToRelative(31f, 24f, 31f, 63f)
            reflectiveQuadToRelative(-31f, 63f)
            lineTo(529f, 602f)
            quadToRelative(-22f, 17f, -49f, 17f)
            reflectiveQuadToRelative(-49f, -17f)
            close()
        }
    }.build()
}
