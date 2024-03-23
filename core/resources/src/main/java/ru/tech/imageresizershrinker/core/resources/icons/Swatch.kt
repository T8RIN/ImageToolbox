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

val Icons.Rounded.Swatch: ImageVector by lazy {
    Builder(
        name = "Swatch", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(20.0f, 14.0f)
            horizontalLineTo(6.0f)
            curveTo(3.8f, 14.0f, 2.0f, 15.8f, 2.0f, 18.0f)
            reflectiveCurveTo(3.8f, 22.0f, 6.0f, 22.0f)
            horizontalLineTo(20.0f)
            curveTo(21.1f, 22.0f, 22.0f, 21.1f, 22.0f, 20.0f)
            verticalLineTo(16.0f)
            curveTo(22.0f, 14.9f, 21.1f, 14.0f, 20.0f, 14.0f)
            moveTo(6.0f, 20.0f)
            curveTo(4.9f, 20.0f, 4.0f, 19.1f, 4.0f, 18.0f)
            reflectiveCurveTo(4.9f, 16.0f, 6.0f, 16.0f)
            reflectiveCurveTo(8.0f, 16.9f, 8.0f, 18.0f)
            reflectiveCurveTo(7.1f, 20.0f, 6.0f, 20.0f)
            moveTo(6.3f, 12.0f)
            lineTo(13.0f, 5.3f)
            curveTo(13.8f, 4.5f, 15.0f, 4.5f, 15.8f, 5.3f)
            lineTo(18.6f, 8.1f)
            curveTo(19.4f, 8.9f, 19.4f, 10.1f, 18.6f, 10.9f)
            lineTo(17.7f, 12.0f)
            horizontalLineTo(6.3f)
            moveTo(2.0f, 13.5f)
            verticalLineTo(4.0f)
            curveTo(2.0f, 2.9f, 2.9f, 2.0f, 4.0f, 2.0f)
            horizontalLineTo(8.0f)
            curveTo(9.1f, 2.0f, 10.0f, 2.9f, 10.0f, 4.0f)
            verticalLineTo(5.5f)
            lineTo(2.0f, 13.5f)
            close()
        }
    }
        .build()
}