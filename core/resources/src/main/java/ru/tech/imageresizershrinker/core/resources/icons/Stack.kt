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

val Icons.Outlined.Stack: ImageVector by lazy {
    Builder(
        name = "Stack", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.0f, 16.54f)
            lineTo(19.37f, 10.8f)
            lineTo(21.0f, 12.07f)
            lineTo(12.0f, 19.07f)
            lineTo(3.0f, 12.07f)
            lineTo(4.62f, 10.81f)
            lineTo(12.0f, 16.54f)
            moveTo(12.0f, 14.0f)
            lineTo(3.0f, 7.0f)
            lineTo(12.0f, 0.0f)
            lineTo(21.0f, 7.0f)
            lineTo(12.0f, 14.0f)
            moveTo(12.0f, 2.53f)
            lineTo(6.26f, 7.0f)
            lineTo(12.0f, 11.47f)
            lineTo(17.74f, 7.0f)
            lineTo(12.0f, 2.53f)
            moveTo(12.0f, 21.47f)
            lineTo(19.37f, 15.73f)
            lineTo(21.0f, 17.0f)
            lineTo(12.0f, 24.0f)
            lineTo(3.0f, 17.0f)
            lineTo(4.62f, 15.74f)
            lineTo(12.0f, 21.47f)
        }
    }.build()
}