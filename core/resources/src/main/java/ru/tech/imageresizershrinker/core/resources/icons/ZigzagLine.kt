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

val Icons.Rounded.ZigzagLine: ImageVector by lazy {
    ImageVector.Builder(
        name = "ZigzagLine",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(19.011f, 5.493f)
            horizontalLineToRelative(-6f)
            curveToRelative(-0.828f, 0f, -1.499f, 0.671f, -1.499f, 1.499f)
            curveToRelative(0f, 0.002f, 0.001f, 0.004f, 0.001f, 0.006f)
            reflectiveCurveToRelative(-0.001f, 0.004f, -0.001f, 0.006f)
            verticalLineToRelative(4.495f)
            horizontalLineTo(6.989f)
            curveToRelative(-0.828f, 0f, -1.499f, 0.671f, -1.499f, 1.499f)
            curveToRelative(0f, 0.002f, 0.001f, 0.004f, 0.001f, 0.006f)
            reflectiveCurveToRelative(-0.001f, 0.004f, -0.001f, 0.006f)
            verticalLineToRelative(6f)
            curveToRelative(0f, 0.828f, 0.671f, 1.499f, 1.499f, 1.499f)
            reflectiveCurveToRelative(1.499f, -0.671f, 1.499f, -1.499f)
            verticalLineToRelative(-4.512f)
            horizontalLineToRelative(4.467f)
            curveToRelative(0.019f, 0.001f, 0.037f, 0.006f, 0.056f, 0.006f)
            curveToRelative(0.828f, 0f, 1.499f, -0.671f, 1.499f, -1.499f)
            verticalLineToRelative(-4.512f)
            horizontalLineToRelative(4.501f)
            curveToRelative(0.828f, 0f, 1.499f, -0.671f, 1.499f, -1.499f)
            reflectiveCurveTo(19.839f, 5.493f, 19.011f, 5.493f)
            close()
        }
    }.build()
}
