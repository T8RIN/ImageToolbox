/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Base64: ImageVector by lazy {
    ImageVector.Builder(
        name = "Base64Rounded",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6.566f, 16.554f)
            horizontalLineToRelative(0.909f)
            verticalLineToRelative(0.909f)
            horizontalLineToRelative(-0.909f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(14.526f, 8.969f)
            lineToRelative(1.756f, 0f)
            lineToRelative(0.307f, -1.756f)
            lineToRelative(-1.756f, 0f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(18.307f, 3f)
            horizontalLineToRelative(-5.259f)
            horizontalLineTo(5.694f)
            horizontalLineTo(5.693f)
            curveTo(4.206f, 3f, 3f, 4.206f, 3f, 5.693f)
            verticalLineToRelative(12.613f)
            curveToRelative(0f, 1.302f, 0.923f, 2.388f, 2.151f, 2.639f)
            curveToRelative(0.105f, 0.021f, 0.215f, 0.024f, 0.324f, 0.033f)
            curveTo(5.548f, 20.984f, 5.618f, 21f, 5.693f, 21f)
            horizontalLineToRelative(0f)
            horizontalLineToRelative(8.294f)
            horizontalLineToRelative(4.319f)
            curveTo(19.794f, 21f, 21f, 19.794f, 21f, 18.307f)
            verticalLineToRelative(-5.259f)
            curveToRelative(0f, 0f, 0f, 0f, 0f, 0f)
            verticalLineToRelative(-7.354f)
            curveTo(21f, 4.206f, 19.794f, 3f, 18.307f, 3f)
            close()
            moveTo(8.383f, 14.737f)
            horizontalLineTo(6.566f)
            verticalLineToRelative(0.909f)
            horizontalLineToRelative(0.909f)
            curveToRelative(0.502f, 0f, 0.908f, 0.407f, 0.908f, 0.909f)
            verticalLineToRelative(0.909f)
            curveToRelative(0f, 0.502f, -0.407f, 0.908f, -0.908f, 0.908f)
            horizontalLineTo(6.566f)
            curveToRelative(-0.502f, 0f, -0.909f, -0.407f, -0.909f, -0.908f)
            verticalLineToRelative(-2.726f)
            curveToRelative(0f, -0.502f, 0.407f, -0.908f, 0.909f, -0.908f)
            horizontalLineToRelative(1.817f)
            verticalLineTo(14.737f)
            close()
            moveTo(11.942f, 18.371f)
            horizontalLineToRelative(-0.909f)
            verticalLineToRelative(-1.817f)
            horizontalLineTo(9.217f)
            verticalLineToRelative(-2.725f)
            horizontalLineToRelative(0.909f)
            verticalLineToRelative(1.817f)
            horizontalLineToRelative(0.908f)
            verticalLineToRelative(-1.817f)
            horizontalLineToRelative(0.909f)
            verticalLineTo(18.371f)
            close()
            moveTo(18.344f, 7.213f)
            horizontalLineToRelative(-0.878f)
            lineTo(17.159f, 8.969f)
            horizontalLineToRelative(0.878f)
            lineToRelative(-0.158f, 0.878f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(-0.154f, 0.878f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(0.154f, -0.878f)
            horizontalLineToRelative(-1.756f)
            lineToRelative(-0.154f, 0.878f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(0.154f, -0.878f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(0.158f, -0.878f)
            horizontalLineToRelative(0.878f)
            lineToRelative(0.307f, -1.756f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(0.154f, -0.878f)
            horizontalLineToRelative(0.878f)
            lineToRelative(0.158f, -0.878f)
            horizontalLineToRelative(0.878f)
            lineToRelative(-0.158f, 0.878f)
            horizontalLineToRelative(1.756f)
            lineToRelative(0.158f, -0.878f)
            horizontalLineToRelative(0.878f)
            lineToRelative(-0.158f, 0.878f)
            horizontalLineToRelative(0.878f)
            lineTo(18.344f, 7.213f)
            close()
        }
    }.build()
}


val Icons.Outlined.Base64: ImageVector by lazy {
    ImageVector.Builder(
        name = "Base64Outlined",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6.566f, 13.829f)
            curveToRelative(-0.502f, 0f, -0.908f, 0.407f, -0.908f, 0.908f)
            verticalLineToRelative(2.725f)
            curveToRelative(0f, 0.502f, 0.407f, 0.908f, 0.908f, 0.908f)
            horizontalLineToRelative(0.908f)
            curveToRelative(0.502f, 0f, 0.908f, -0.407f, 0.908f, -0.908f)
            verticalLineToRelative(-0.908f)
            curveToRelative(0f, -0.502f, -0.407f, -0.908f, -0.908f, -0.908f)
            horizontalLineToRelative(-0.908f)
            verticalLineToRelative(-0.908f)
            horizontalLineToRelative(1.817f)
            verticalLineToRelative(-0.908f)
            horizontalLineTo(6.566f)
            moveTo(6.566f, 16.554f)
            horizontalLineToRelative(0.908f)
            verticalLineToRelative(0.908f)
            horizontalLineToRelative(-0.908f)
            verticalLineTo(16.554f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(9.217f, 13.829f)
            verticalLineToRelative(2.725f)
            horizontalLineToRelative(1.817f)
            verticalLineToRelative(1.817f)
            horizontalLineToRelative(0.908f)
            verticalLineToRelative(-4.542f)
            horizontalLineToRelative(-0.908f)
            verticalLineToRelative(1.817f)
            horizontalLineToRelative(-0.908f)
            verticalLineToRelative(-1.817f)
            horizontalLineTo(9.217f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(18.344f, 7.213f)
            lineToRelative(0.154f, -0.878f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(0.158f, -0.878f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(-0.158f, 0.878f)
            horizontalLineToRelative(-1.756f)
            lineToRelative(0.158f, -0.878f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(-0.158f, 0.878f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(-0.154f, 0.878f)
            horizontalLineToRelative(0.878f)
            lineToRelative(-0.307f, 1.756f)
            horizontalLineToRelative(-0.878f)
            lineTo(12.612f, 9.847f)
            horizontalLineToRelative(0.878f)
            lineToRelative(-0.154f, 0.878f)
            horizontalLineToRelative(0.878f)
            lineToRelative(0.154f, -0.878f)
            horizontalLineToRelative(1.756f)
            lineToRelative(-0.154f, 0.878f)
            horizontalLineToRelative(0.878f)
            lineToRelative(0.154f, -0.878f)
            horizontalLineToRelative(0.878f)
            lineToRelative(0.158f, -0.878f)
            horizontalLineToRelative(-0.878f)
            lineToRelative(0.307f, -1.756f)
            horizontalLineTo(18.344f)
            close()
            moveTo(16.281f, 8.969f)
            horizontalLineToRelative(-1.756f)
            lineToRelative(0.307f, -1.756f)
            horizontalLineToRelative(1.756f)
            lineTo(16.281f, 8.969f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(21f, 5.693f)
            curveTo(21f, 4.206f, 19.794f, 3f, 18.307f, 3f)
            horizontalLineToRelative(-5.259f)
            horizontalLineTo(5.694f)
            horizontalLineTo(5.693f)
            curveTo(4.206f, 3f, 3f, 4.206f, 3f, 5.693f)
            verticalLineToRelative(12.613f)
            curveToRelative(0f, 1.302f, 0.923f, 2.388f, 2.151f, 2.639f)
            curveToRelative(0.105f, 0.021f, 0.215f, 0.024f, 0.324f, 0.033f)
            curveTo(5.548f, 20.984f, 5.618f, 21f, 5.693f, 21f)
            horizontalLineToRelative(0f)
            horizontalLineToRelative(8.294f)
            horizontalLineToRelative(4.319f)
            curveTo(19.794f, 21f, 21f, 19.794f, 21f, 18.307f)
            verticalLineToRelative(-5.259f)
            curveToRelative(0f, 0f, 0f, 0f, 0f, 0f)
            verticalLineTo(5.693f)
            close()
            moveTo(19f, 17.715f)
            curveTo(19f, 18.424f, 18.424f, 19f, 17.715f, 19f)
            horizontalLineToRelative(-3.737f)
            horizontalLineTo(6.286f)
            curveToRelative(-0.71f, 0f, -1.285f, -0.576f, -1.285f, -1.285f)
            verticalLineTo(6.285f)
            curveTo(5f, 5.576f, 5.576f, 5f, 6.286f, 5f)
            horizontalLineToRelative(6.778f)
            horizontalLineToRelative(4.651f)
            curveToRelative(0.71f, 0f, 1.285f, 0.576f, 1.285f, 1.285f)
            verticalLineToRelative(3.737f)
            curveToRelative(-0f, 0f, -0f, 0f, -0f, 0f)
            verticalLineTo(17.715f)
            close()
        }
    }.build()
}