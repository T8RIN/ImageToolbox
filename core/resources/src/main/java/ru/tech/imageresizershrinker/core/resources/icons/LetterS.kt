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

val Icons.Filled.LetterS: ImageVector by lazy {
    Builder(
        name = "Letter S", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth =
        24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.0f, 7.0f)
            curveTo(9.9f, 7.0f, 9.0f, 7.9f, 9.0f, 9.0f)
            verticalLineTo(11.0f)
            curveTo(9.0f, 12.11f, 9.9f, 13.0f, 11.0f, 13.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(9.0f)
            verticalLineTo(17.0f)
            horizontalLineTo(13.0f)
            curveTo(14.11f, 17.0f, 15.0f, 16.11f, 15.0f, 15.0f)
            verticalLineTo(13.0f)
            curveTo(15.0f, 11.9f, 14.11f, 11.0f, 13.0f, 11.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(15.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(11.0f)
            close()
        }
    }.build()
}
