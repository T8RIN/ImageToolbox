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

val Icons.Rounded.Boosty: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Boosty",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(2.661f, 14.337f)
            lineTo(6.801f, 0f)
            horizontalLineToRelative(6.362f)
            lineTo(11.88f, 4.444f)
            lineToRelative(-0.038f, 0.077f)
            lineToRelative(-3.378f, 11.733f)
            horizontalLineToRelative(3.15f)
            curveToRelative(-1.321f, 3.289f, -2.35f, 5.867f, -3.086f, 7.733f)
            curveToRelative(-5.816f, -0.063f, -7.442f, -4.228f, -6.02f, -9.155f)
            moveTo(8.554f, 24f)
            lineToRelative(7.67f, -11.035f)
            horizontalLineToRelative(-3.25f)
            lineToRelative(2.83f, -7.073f)
            curveToRelative(4.852f, 0.508f, 7.137f, 4.33f, 5.791f, 8.952f)
            curveTo(20.16f, 19.81f, 14.344f, 24f, 8.68f, 24f)
            horizontalLineToRelative(-0.127f)
            close()
        }
    }.build()
}
