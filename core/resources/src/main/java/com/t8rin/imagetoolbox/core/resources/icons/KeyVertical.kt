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

val Icons.Rounded.KeyVertical: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.KeyVertical",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(565f, 365f)
            quadToRelative(35f, -35f, 35f, -85f)
            reflectiveQuadToRelative(-35f, -85f)
            quadToRelative(-35f, -35f, -85f, -35f)
            reflectiveQuadToRelative(-85f, 35f)
            quadToRelative(-35f, 35f, -35f, 85f)
            reflectiveQuadToRelative(35f, 85f)
            quadToRelative(35f, 35f, 85f, 35f)
            reflectiveQuadToRelative(85f, -35f)
            close()
            moveTo(466f, 902.5f)
            quadToRelative(-7f, -2.5f, -13f, -7.5f)
            lineToRelative(-103f, -90f)
            quadToRelative(-6f, -5f, -9.5f, -11.5f)
            reflectiveQuadTo(336f, 779f)
            quadToRelative(-1f, -8f, 1.5f, -15.5f)
            reflectiveQuadTo(345f, 750f)
            lineToRelative(55f, -70f)
            lineToRelative(-52f, -52f)
            quadToRelative(-6f, -6f, -8.5f, -13f)
            reflectiveQuadToRelative(-2.5f, -15f)
            quadToRelative(0f, -8f, 2.5f, -15f)
            reflectiveQuadToRelative(8.5f, -13f)
            lineToRelative(52f, -52f)
            verticalLineToRelative(-14f)
            quadToRelative(-72f, -25f, -116f, -87f)
            reflectiveQuadToRelative(-44f, -139f)
            quadToRelative(0f, -100f, 70f, -170f)
            reflectiveQuadToRelative(170f, -70f)
            quadToRelative(100f, 0f, 170f, 70f)
            reflectiveQuadToRelative(70f, 170f)
            quadToRelative(0f, 81f, -46f, 141.5f)
            reflectiveQuadTo(560f, 506f)
            verticalLineToRelative(318f)
            quadToRelative(0f, 8f, -3f, 15.5f)
            reflectiveQuadToRelative(-9f, 13.5f)
            lineToRelative(-41f, 41f)
            quadToRelative(-5f, 5f, -11.5f, 8f)
            reflectiveQuadTo(481f, 905f)
            quadToRelative(-8f, 0f, -15f, -2.5f)
            close()
        }
    }.build()
}
