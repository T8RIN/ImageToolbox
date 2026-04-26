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

val Icons.Rounded.Emergency: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Emergency",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(410f, 770f)
            verticalLineToRelative(-168f)
            lineToRelative(-146f, 84f)
            quadToRelative(-25f, 14f, -53f, 7f)
            reflectiveQuadToRelative(-42f, -33f)
            quadToRelative(-14f, -25f, -7f, -53f)
            reflectiveQuadToRelative(32f, -42f)
            lineToRelative(146f, -85f)
            lineToRelative(-146f, -84f)
            quadToRelative(-25f, -14f, -32f, -42.5f)
            reflectiveQuadToRelative(7f, -53.5f)
            quadToRelative(14f, -25f, 42f, -32f)
            reflectiveQuadToRelative(53f, 7f)
            lineToRelative(146f, 84f)
            verticalLineToRelative(-169f)
            quadToRelative(0f, -29f, 20.5f, -49.5f)
            reflectiveQuadTo(480f, 120f)
            quadToRelative(29f, 0f, 49.5f, 20.5f)
            reflectiveQuadTo(550f, 190f)
            verticalLineToRelative(169f)
            lineToRelative(146f, -84f)
            quadToRelative(25f, -14f, 53f, -7f)
            reflectiveQuadToRelative(42f, 32f)
            quadToRelative(14f, 25f, 6.5f, 53.5f)
            reflectiveQuadTo(765f, 396f)
            lineToRelative(-145f, 84f)
            lineToRelative(146f, 85f)
            quadToRelative(25f, 14f, 32f, 42f)
            reflectiveQuadToRelative(-7f, 54f)
            quadToRelative(-14f, 25f, -42f, 32f)
            reflectiveQuadToRelative(-53f, -7f)
            lineToRelative(-146f, -84f)
            verticalLineToRelative(168f)
            quadToRelative(0f, 29f, -20.5f, 49.5f)
            reflectiveQuadTo(480f, 840f)
            quadToRelative(-29f, 0f, -49.5f, -20.5f)
            reflectiveQuadTo(410f, 770f)
            close()
        }
    }.build()
}
