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

val Icons.Rounded.ArrowDropDown: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.ArrowDropDown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(459f, 579f)
            lineTo(314f, 434f)
            quadToRelative(-3f, -3f, -4.5f, -6.5f)
            reflectiveQuadTo(308f, 420f)
            quadToRelative(0f, -8f, 5.5f, -14f)
            reflectiveQuadToRelative(14.5f, -6f)
            horizontalLineToRelative(304f)
            quadToRelative(9f, 0f, 14.5f, 6f)
            reflectiveQuadToRelative(5.5f, 14f)
            quadToRelative(0f, 2f, -6f, 14f)
            lineTo(501f, 579f)
            quadToRelative(-5f, 5f, -10f, 7f)
            reflectiveQuadToRelative(-11f, 2f)
            quadToRelative(-6f, 0f, -11f, -2f)
            reflectiveQuadToRelative(-10f, -7f)
            close()
        }
    }.build()
}
