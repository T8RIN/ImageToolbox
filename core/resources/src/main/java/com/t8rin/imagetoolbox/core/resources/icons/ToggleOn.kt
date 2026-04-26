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

val Icons.Outlined.ToggleOn: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ToggleOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(280f, 720f)
            quadToRelative(-100f, 0f, -170f, -70f)
            reflectiveQuadTo(40f, 480f)
            quadToRelative(0f, -100f, 70f, -170f)
            reflectiveQuadToRelative(170f, -70f)
            horizontalLineToRelative(400f)
            quadToRelative(100f, 0f, 170f, 70f)
            reflectiveQuadToRelative(70f, 170f)
            quadToRelative(0f, 100f, -70f, 170f)
            reflectiveQuadToRelative(-170f, 70f)
            lineTo(280f, 720f)
            close()
            moveTo(280f, 640f)
            horizontalLineToRelative(400f)
            quadToRelative(66f, 0f, 113f, -47f)
            reflectiveQuadToRelative(47f, -113f)
            quadToRelative(0f, -66f, -47f, -113f)
            reflectiveQuadToRelative(-113f, -47f)
            lineTo(280f, 320f)
            quadToRelative(-66f, 0f, -113f, 47f)
            reflectiveQuadToRelative(-47f, 113f)
            quadToRelative(0f, 66f, 47f, 113f)
            reflectiveQuadToRelative(113f, 47f)
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
            moveTo(480f, 480f)
            close()
        }
    }.build()
}
