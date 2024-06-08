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

val Icons.Rounded.Firebase: ImageVector by lazy {
    Builder(
        name = "Firebase", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFFFF9100)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(8.4f, 23.3f)
            curveToRelative(1.0f, 0.4f, 2.1f, 0.6f, 3.2f, 0.7f)
            curveToRelative(1.5f, 0.1f, 3.0f, -0.3f, 4.3f, -0.9f)
            curveToRelative(-1.6f, -0.6f, -3.0f, -1.5f, -4.2f, -2.7f)
            curveTo(11.0f, 21.7f, 9.8f, 22.7f, 8.4f, 23.3f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFFFC400)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.8f, 20.5f)
            curveTo(9.0f, 17.9f, 7.3f, 14.2f, 7.4f, 10.1f)
            curveToRelative(0.0f, -0.1f, 0.0f, -0.3f, 0.0f, -0.4f)
            curveTo(7.0f, 9.5f, 6.4f, 9.5f, 5.9f, 9.5f)
            curveToRelative(-0.8f, 0.0f, -1.5f, 0.1f, -2.2f, 0.3f)
            curveTo(3.0f, 11.0f, 2.5f, 12.5f, 2.5f, 14.1f)
            curveToRelative(-0.1f, 4.1f, 2.4f, 7.7f, 6.0f, 9.2f)
            curveTo(9.8f, 22.7f, 11.0f, 21.7f, 11.8f, 20.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFFF9100)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.8f, 20.5f)
            curveToRelative(0.6f, -1.0f, 1.0f, -2.3f, 1.1f, -3.6f)
            curveToRelative(0.1f, -3.4f, -2.2f, -6.4f, -5.4f, -7.2f)
            curveToRelative(0.0f, 0.1f, 0.0f, 0.3f, 0.0f, 0.4f)
            curveTo(7.3f, 14.2f, 9.0f, 17.9f, 11.8f, 20.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFDD2C00)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.5f, 0.0f)
            curveToRelative(-1.8f, 1.5f, -3.3f, 3.4f, -4.1f, 5.6f)
            curveTo(7.9f, 6.9f, 7.6f, 8.2f, 7.5f, 9.7f)
            curveToRelative(3.2f, 0.8f, 5.5f, 3.8f, 5.4f, 7.2f)
            curveToRelative(0.0f, 1.3f, -0.4f, 2.5f, -1.1f, 3.6f)
            curveToRelative(1.2f, 1.1f, 2.6f, 2.0f, 4.2f, 2.7f)
            curveToRelative(3.2f, -1.5f, 5.4f, -4.6f, 5.5f, -8.3f)
            curveToRelative(0.1f, -2.4f, -0.8f, -4.6f, -2.2f, -6.4f)
            curveTo(18.0f, 6.5f, 12.5f, 0.0f, 12.5f, 0.0f)
            close()
        }
    }.build()
}