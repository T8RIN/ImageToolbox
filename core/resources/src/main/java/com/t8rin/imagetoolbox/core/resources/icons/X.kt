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

val Icons.Rounded.X: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.X",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(14.234f, 10.162f)
            lineTo(22.977f, 0f)
            horizontalLineToRelative(-2.072f)
            lineToRelative(-7.591f, 8.824f)
            lineTo(7.251f, 0f)
            horizontalLineTo(0.258f)
            lineToRelative(9.168f, 13.343f)
            lineTo(0.258f, 24f)
            horizontalLineTo(2.33f)
            lineToRelative(8.016f, -9.318f)
            lineTo(16.749f, 24f)
            horizontalLineToRelative(6.993f)
            close()
            moveToRelative(-2.837f, 3.299f)
            lineToRelative(-0.929f, -1.329f)
            lineTo(3.076f, 1.56f)
            horizontalLineToRelative(3.182f)
            lineToRelative(5.965f, 8.532f)
            lineToRelative(0.929f, 1.329f)
            lineToRelative(7.754f, 11.09f)
            horizontalLineToRelative(-3.182f)
            close()
        }
    }.build()
}
