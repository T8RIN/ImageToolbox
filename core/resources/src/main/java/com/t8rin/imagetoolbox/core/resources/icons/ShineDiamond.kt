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

val Icons.Outlined.ShineDiamond: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ShineDiamond",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 880f)
            lineTo(120f, 524f)
            lineToRelative(200f, -244f)
            horizontalLineToRelative(320f)
            lineToRelative(200f, 244f)
            lineTo(480f, 880f)
            close()
            moveTo(183f, 280f)
            lineToRelative(-85f, -85f)
            lineToRelative(57f, -56f)
            lineToRelative(85f, 85f)
            lineToRelative(-57f, 56f)
            close()
            moveTo(440f, 200f)
            verticalLineToRelative(-120f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(120f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(775f, 280f)
            lineTo(718f, 223f)
            lineTo(803f, 138f)
            lineTo(860f, 195f)
            lineTo(775f, 280f)
            close()
            moveTo(480f, 768f)
            lineToRelative(210f, -208f)
            lineTo(270f, 560f)
            lineToRelative(210f, 208f)
            close()
            moveTo(358f, 360f)
            lineToRelative(-99f, 120f)
            horizontalLineToRelative(442f)
            lineToRelative(-99f, -120f)
            lineTo(358f, 360f)
            close()
        }
    }.build()
}
