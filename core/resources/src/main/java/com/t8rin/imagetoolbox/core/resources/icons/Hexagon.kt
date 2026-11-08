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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.Hexagon: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Hexagon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(318f, -120f)
                quadToRelative(-22f, 0f, -40f, -10.5f)
                reflectiveQuadTo(249f, -160f)
                lineTo(87f, -440f)
                quadToRelative(-11f, -19f, -11f, -40f)
                reflectiveQuadToRelative(11f, -40f)
                lineToRelative(162f, -280f)
                quadToRelative(11f, -19f, 29f, -29.5f)
                reflectiveQuadToRelative(40f, -10.5f)
                horizontalLineToRelative(324f)
                quadToRelative(22f, 0f, 40f, 10.5f)
                reflectiveQuadToRelative(29f, 29.5f)
                lineToRelative(162f, 280f)
                quadToRelative(11f, 19f, 11f, 40f)
                reflectiveQuadToRelative(-11f, 40f)
                lineTo(711f, -160f)
                quadToRelative(-11f, 19f, -29f, 29.5f)
                reflectiveQuadTo(642f, -120f)
                horizontalLineTo(318f)
                close()
                moveToRelative(0f, -80f)
                horizontalLineToRelative(324f)
                lineToRelative(161f, -280f)
                lineToRelative(-161f, -280f)
                horizontalLineTo(318f)
                lineTo(156f, -480f)
                lineToRelative(162f, 280f)
                close()
                moveToRelative(162f, -280f)
                close()
            }
        }
    }.build()
}