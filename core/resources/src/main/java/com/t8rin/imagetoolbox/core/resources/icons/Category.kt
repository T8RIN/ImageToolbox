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

val Icons.Outlined.Category: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Category",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(260f, 440f)
            lineToRelative(220f, -360f)
            lineToRelative(220f, 360f)
            lineTo(260f, 440f)
            close()
            moveTo(700f, 880f)
            quadToRelative(-75f, 0f, -127.5f, -52.5f)
            reflectiveQuadTo(520f, 700f)
            quadToRelative(0f, -75f, 52.5f, -127.5f)
            reflectiveQuadTo(700f, 520f)
            quadToRelative(75f, 0f, 127.5f, 52.5f)
            reflectiveQuadTo(880f, 700f)
            quadToRelative(0f, 75f, -52.5f, 127.5f)
            reflectiveQuadTo(700f, 880f)
            close()
            moveTo(120f, 860f)
            verticalLineToRelative(-320f)
            horizontalLineToRelative(320f)
            verticalLineToRelative(320f)
            lineTo(120f, 860f)
            close()
            moveTo(700f, 800f)
            quadToRelative(42f, 0f, 71f, -29f)
            reflectiveQuadToRelative(29f, -71f)
            quadToRelative(0f, -42f, -29f, -71f)
            reflectiveQuadToRelative(-71f, -29f)
            quadToRelative(-42f, 0f, -71f, 29f)
            reflectiveQuadToRelative(-29f, 71f)
            quadToRelative(0f, 42f, 29f, 71f)
            reflectiveQuadToRelative(71f, 29f)
            close()
            moveTo(200f, 780f)
            horizontalLineToRelative(160f)
            verticalLineToRelative(-160f)
            lineTo(200f, 620f)
            verticalLineToRelative(160f)
            close()
            moveTo(402f, 360f)
            horizontalLineToRelative(156f)
            lineToRelative(-78f, -126f)
            lineToRelative(-78f, 126f)
            close()
            moveTo(480f, 360f)
            close()
            moveTo(360f, 620f)
            close()
            moveTo(700f, 700f)
            close()
        }
    }.build()
}
