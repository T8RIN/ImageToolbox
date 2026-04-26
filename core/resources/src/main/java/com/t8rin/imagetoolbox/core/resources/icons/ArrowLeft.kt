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

val Icons.AutoMirrored.Rounded.ArrowLeft: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "AutoMirrored.Rounded.ArrowLeft",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f,
        autoMirror = true
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(526f, 646f)
            lineTo(381f, 501f)
            quadToRelative(-5f, -5f, -7f, -10f)
            reflectiveQuadToRelative(-2f, -11f)
            quadToRelative(0f, -6f, 2f, -11f)
            reflectiveQuadToRelative(7f, -10f)
            lineToRelative(145f, -145f)
            quadToRelative(3f, -3f, 6.5f, -4.5f)
            reflectiveQuadToRelative(7.5f, -1.5f)
            quadToRelative(8f, 0f, 14f, 5.5f)
            reflectiveQuadToRelative(6f, 14.5f)
            verticalLineToRelative(304f)
            quadToRelative(0f, 9f, -6f, 14.5f)
            reflectiveQuadToRelative(-14f, 5.5f)
            quadToRelative(-2f, 0f, -14f, -6f)
            close()
        }
    }.build()
}
