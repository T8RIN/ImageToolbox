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

val Icons.Rounded.LocationSearching: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.LocationSearching",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(440f, 880f)
            verticalLineToRelative(-40f)
            quadToRelative(-125f, -14f, -214.5f, -103.5f)
            reflectiveQuadTo(122f, 522f)
            lineTo(82f, 522f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(42f, 482f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(82f, 442f)
            horizontalLineToRelative(40f)
            quadToRelative(14f, -125f, 103.5f, -214.5f)
            reflectiveQuadTo(440f, 124f)
            verticalLineToRelative(-40f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(480f, 44f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(520f, 84f)
            verticalLineToRelative(40f)
            quadToRelative(125f, 14f, 214.5f, 103.5f)
            reflectiveQuadTo(838f, 442f)
            horizontalLineToRelative(40f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(918f, 482f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(878f, 522f)
            horizontalLineToRelative(-40f)
            quadToRelative(-14f, 125f, -103.5f, 214.5f)
            reflectiveQuadTo(520f, 840f)
            verticalLineToRelative(40f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 920f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(440f, 880f)
            close()
            moveTo(480f, 762f)
            quadToRelative(116f, 0f, 198f, -82f)
            reflectiveQuadToRelative(82f, -198f)
            quadToRelative(0f, -116f, -82f, -198f)
            reflectiveQuadToRelative(-198f, -82f)
            quadToRelative(-116f, 0f, -198f, 82f)
            reflectiveQuadToRelative(-82f, 198f)
            quadToRelative(0f, 116f, 82f, 198f)
            reflectiveQuadToRelative(198f, 82f)
            close()
        }
    }.build()
}
