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

val Icons.Outlined.Shield: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Shield",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 876f)
            quadToRelative(-7f, 0f, -13f, -1f)
            reflectiveQuadToRelative(-12f, -3f)
            quadToRelative(-135f, -45f, -215f, -166.5f)
            reflectiveQuadTo(160f, 444f)
            verticalLineToRelative(-189f)
            quadToRelative(0f, -25f, 14.5f, -45f)
            reflectiveQuadToRelative(37.5f, -29f)
            lineToRelative(240f, -90f)
            quadToRelative(14f, -5f, 28f, -5f)
            reflectiveQuadToRelative(28f, 5f)
            lineToRelative(240f, 90f)
            quadToRelative(23f, 9f, 37.5f, 29f)
            reflectiveQuadToRelative(14.5f, 45f)
            verticalLineToRelative(189f)
            quadToRelative(0f, 140f, -80f, 261.5f)
            reflectiveQuadTo(505f, 872f)
            quadToRelative(-6f, 2f, -12f, 3f)
            reflectiveQuadToRelative(-13f, 1f)
            close()
            moveTo(480f, 796f)
            quadToRelative(104f, -33f, 172f, -132f)
            reflectiveQuadToRelative(68f, -220f)
            verticalLineToRelative(-189f)
            lineToRelative(-240f, -90f)
            lineToRelative(-240f, 90f)
            verticalLineToRelative(189f)
            quadToRelative(0f, 121f, 68f, 220f)
            reflectiveQuadToRelative(172f, 132f)
            close()
            moveTo(480f, 480f)
            close()
        }
    }.build()
}
