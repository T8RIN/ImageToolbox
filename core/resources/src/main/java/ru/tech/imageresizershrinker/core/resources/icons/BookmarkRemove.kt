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

val Icons.Rounded.BookmarkRemove: ImageVector by lazy {
    Builder(
        name = "Bookmark Remove", defaultWidth = 24.0.dp, defaultHeight
        = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(17.0f, 3.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 19.0f,
                y1 = 5.0f
            )
            verticalLineTo(21.0f)
            lineTo(12.0f, 18.0f)
            lineTo(5.0f, 21.0f)
            verticalLineTo(5.0f)
            curveTo(5.0f, 3.89f, 5.9f, 3.0f, 7.0f, 3.0f)
            horizontalLineTo(17.0f)
            moveTo(8.17f, 8.58f)
            lineTo(10.59f, 11.0f)
            lineTo(8.17f, 13.41f)
            lineTo(9.59f, 14.83f)
            lineTo(12.0f, 12.41f)
            lineTo(14.41f, 14.83f)
            lineTo(15.83f, 13.41f)
            lineTo(13.41f, 11.0f)
            lineTo(15.83f, 8.58f)
            lineTo(14.41f, 7.17f)
            lineTo(12.0f, 9.58f)
            lineTo(9.59f, 7.17f)
            lineTo(8.17f, 8.58f)
            close()
        }
    }
        .build()
}