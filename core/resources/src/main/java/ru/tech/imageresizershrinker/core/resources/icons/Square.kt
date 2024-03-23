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
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Square: ImageVector by lazy {
    Builder(
        name = "Square", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(20.4125f, 3.5875f)
            curveTo(20.0208f, 3.1959f, 19.55f, 3.0f, 19.0f, 3.0f)
            horizontalLineTo(5.0f)
            curveTo(4.45f, 3.0f, 3.9792f, 3.1959f, 3.5875f, 3.5875f)
            reflectiveCurveTo(3.0f, 4.45f, 3.0f, 5.0f)
            verticalLineToRelative(14.0f)
            curveToRelative(0.0f, 0.55f, 0.1959f, 1.0208f, 0.5875f, 1.4125f)
            reflectiveCurveTo(4.45f, 21.0f, 5.0f, 21.0f)
            horizontalLineToRelative(14.0f)
            curveToRelative(0.55f, 0.0f, 1.0208f, -0.1959f, 1.4125f, -0.5875f)
            reflectiveCurveTo(21.0f, 19.55f, 21.0f, 19.0f)
            verticalLineTo(5.0f)
            curveTo(21.0f, 4.45f, 20.8041f, 3.9792f, 20.4125f, 3.5875f)
            close()
        }
    }.build()
}