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

val Icons.Rounded.Ton: ImageVector by lazy {
    Builder(
        name = "Ton", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth
        = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(18.0777f, 3.0f)
            horizontalLineTo(5.9216f)
            curveTo(3.6865f, 3.0f, 2.2699f, 5.4109f, 3.3943f, 7.3599f)
            lineToRelative(7.5023f, 13.0033f)
            curveToRelative(0.4896f, 0.8491f, 1.7165f, 0.8491f, 2.206f, 0.0f)
            lineToRelative(7.5038f, -13.0033f)
            curveTo(21.7294f, 5.414f, 20.3128f, 3.0f, 18.0792f, 3.0f)
            horizontalLineTo(18.0777f)
            close()
            moveTo(10.8905f, 16.4637f)
            lineToRelative(-1.6339f, -3.1621f)
            lineTo(5.3143f, 6.2508f)
            curveToRelative(-0.2601f, -0.4513f, 0.0612f, -1.0296f, 0.6058f, -1.0296f)
            horizontalLineToRelative(4.9689f)
            verticalLineToRelative(11.244f)
            lineTo(10.8905f, 16.4637f)
            close()
            moveTo(18.682f, 6.2493f)
            lineToRelative(-3.9409f, 7.0539f)
            lineToRelative(-1.6339f, 3.1605f)
            verticalLineTo(5.2197f)
            horizontalLineToRelative(4.9689f)
            curveTo(18.6208f, 5.2197f, 18.942f, 5.798f, 18.682f, 6.2493f)
            close()
        }
    }.build()
}