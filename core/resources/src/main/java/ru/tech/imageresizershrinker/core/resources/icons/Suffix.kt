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

val Icons.Filled.Suffix: ImageVector by lazy {
    Builder(
        name = "Suffix", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.8f, 4.6f)
            horizontalLineToRelative(-1.5f)
            lineToRelative(-4.8f, 11.0f)
            horizontalLineToRelative(2.1f)
            lineToRelative(0.9f, -2.2f)
            horizontalLineToRelative(5.0f)
            lineToRelative(0.9f, 2.2f)
            horizontalLineToRelative(2.1f)
            lineTo(12.8f, 4.6f)
            close()
            moveTo(10.1f, 11.6f)
            lineToRelative(1.9f, -5.0f)
            lineToRelative(1.9f, 5.0f)
            horizontalLineTo(10.1f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(19.0f, 14.7f)
            lineToRelative(0.0f, 2.5f)
            lineToRelative(-12.0f, 0.0f)
            lineToRelative(-2.3f, 0.0f)
            lineToRelative(-1.3f, 0.0f)
            lineToRelative(0.0f, 2.0f)
            lineToRelative(1.3f, 0.0f)
            lineToRelative(2.3f, 0.0f)
            lineToRelative(12.0f, 0.0f)
            lineToRelative(2.0f, 0.0f)
            lineToRelative(0.0f, -2.0f)
            lineToRelative(0.0f, -2.5f)
            close()
        }
    }
        .build()
}