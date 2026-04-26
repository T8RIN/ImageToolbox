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

val Icons.Rounded.LinearScale: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.LinearScale",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(680f, 680f)
            quadToRelative(-72f, 0f, -127f, -45.5f)
            reflectiveQuadTo(484f, 520f)
            lineTo(272f, 520f)
            quadToRelative(-12f, 27f, -37f, 43.5f)
            reflectiveQuadTo(180f, 580f)
            quadToRelative(-42f, 0f, -71f, -29f)
            reflectiveQuadToRelative(-29f, -71f)
            quadToRelative(0f, -42f, 29f, -71f)
            reflectiveQuadToRelative(71f, -29f)
            quadToRelative(30f, 0f, 55f, 16.5f)
            reflectiveQuadToRelative(37f, 43.5f)
            horizontalLineToRelative(212f)
            quadToRelative(14f, -69f, 69f, -114.5f)
            reflectiveQuadTo(680f, 280f)
            quadToRelative(83f, 0f, 141.5f, 58.5f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 83f, -58.5f, 141.5f)
            reflectiveQuadTo(680f, 680f)
            close()
            moveTo(680f, 600f)
            quadToRelative(50f, 0f, 85f, -35f)
            reflectiveQuadToRelative(35f, -85f)
            quadToRelative(0f, -50f, -35f, -85f)
            reflectiveQuadToRelative(-85f, -35f)
            quadToRelative(-50f, 0f, -85f, 35f)
            reflectiveQuadToRelative(-35f, 85f)
            quadToRelative(0f, 50f, 35f, 85f)
            reflectiveQuadToRelative(85f, 35f)
            close()
        }
    }.build()
}
