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

val Icons.Rounded.ImageSync: ImageVector by lazy {
    Builder(
        name = "ImageSync", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(8.5f, 13.5f)
            lineTo(5.0f, 18.0f)
            horizontalLineTo(13.03f)
            curveTo(13.11f, 19.1f, 13.47f, 20.12f, 14.03f, 21.0f)
            horizontalLineTo(5.0f)
            curveTo(3.9f, 21.0f, 3.0f, 20.11f, 3.0f, 19.0f)
            verticalLineTo(5.0f)
            curveTo(3.0f, 3.9f, 3.9f, 3.0f, 5.0f, 3.0f)
            horizontalLineTo(19.0f)
            curveTo(20.1f, 3.0f, 21.0f, 3.89f, 21.0f, 5.0f)
            verticalLineTo(11.18f)
            curveTo(20.5f, 11.07f, 20.0f, 11.0f, 19.5f, 11.0f)
            curveTo(17.78f, 11.0f, 16.23f, 11.67f, 15.07f, 12.76f)
            lineTo(14.5f, 12.0f)
            lineTo(11.0f, 16.5f)
            lineTo(8.5f, 13.5f)
            moveTo(19.0f, 20.0f)
            curveTo(17.62f, 20.0f, 16.5f, 18.88f, 16.5f, 17.5f)
            curveTo(16.5f, 17.1f, 16.59f, 16.72f, 16.76f, 16.38f)
            lineTo(15.67f, 15.29f)
            curveTo(15.25f, 15.92f, 15.0f, 16.68f, 15.0f, 17.5f)
            curveTo(15.0f, 19.71f, 16.79f, 21.5f, 19.0f, 21.5f)
            verticalLineTo(23.0f)
            lineTo(21.25f, 20.75f)
            lineTo(19.0f, 18.5f)
            verticalLineTo(20.0f)
            moveTo(19.0f, 13.5f)
            verticalLineTo(12.0f)
            lineTo(16.75f, 14.25f)
            lineTo(19.0f, 16.5f)
            verticalLineTo(15.0f)
            curveTo(20.38f, 15.0f, 21.5f, 16.12f, 21.5f, 17.5f)
            curveTo(21.5f, 17.9f, 21.41f, 18.28f, 21.24f, 18.62f)
            lineTo(22.33f, 19.71f)
            curveTo(22.75f, 19.08f, 23.0f, 18.32f, 23.0f, 17.5f)
            curveTo(23.0f, 15.29f, 21.21f, 13.5f, 19.0f, 13.5f)
            close()
        }
    }.build()
}

val Icons.Outlined.ImageSync: ImageVector by lazy {
    Builder(
        name = "ImageSync Outline", defaultWidth = 24.0.dp, defaultHeight
        = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(13.18f, 19.0f)
            curveTo(13.35f, 19.72f, 13.64f, 20.39f, 14.03f, 21.0f)
            horizontalLineTo(5.0f)
            curveTo(3.9f, 21.0f, 3.0f, 20.11f, 3.0f, 19.0f)
            verticalLineTo(5.0f)
            curveTo(3.0f, 3.9f, 3.9f, 3.0f, 5.0f, 3.0f)
            horizontalLineTo(19.0f)
            curveTo(20.11f, 3.0f, 21.0f, 3.9f, 21.0f, 5.0f)
            verticalLineTo(11.18f)
            curveTo(20.5f, 11.07f, 20.0f, 11.0f, 19.5f, 11.0f)
            curveTo(19.33f, 11.0f, 19.17f, 11.0f, 19.0f, 11.03f)
            verticalLineTo(5.0f)
            horizontalLineTo(5.0f)
            verticalLineTo(19.0f)
            horizontalLineTo(13.18f)
            moveTo(11.21f, 15.83f)
            lineTo(9.25f, 13.47f)
            lineTo(6.5f, 17.0f)
            horizontalLineTo(13.03f)
            curveTo(13.14f, 15.54f, 13.73f, 14.22f, 14.64f, 13.19f)
            lineTo(13.96f, 12.29f)
            lineTo(11.21f, 15.83f)
            moveTo(19.0f, 13.5f)
            verticalLineTo(12.0f)
            lineTo(16.75f, 14.25f)
            lineTo(19.0f, 16.5f)
            verticalLineTo(15.0f)
            curveTo(20.38f, 15.0f, 21.5f, 16.12f, 21.5f, 17.5f)
            curveTo(21.5f, 17.9f, 21.41f, 18.28f, 21.24f, 18.62f)
            lineTo(22.33f, 19.71f)
            curveTo(22.75f, 19.08f, 23.0f, 18.32f, 23.0f, 17.5f)
            curveTo(23.0f, 15.29f, 21.21f, 13.5f, 19.0f, 13.5f)
            moveTo(19.0f, 20.0f)
            curveTo(17.62f, 20.0f, 16.5f, 18.88f, 16.5f, 17.5f)
            curveTo(16.5f, 17.1f, 16.59f, 16.72f, 16.76f, 16.38f)
            lineTo(15.67f, 15.29f)
            curveTo(15.25f, 15.92f, 15.0f, 16.68f, 15.0f, 17.5f)
            curveTo(15.0f, 19.71f, 16.79f, 21.5f, 19.0f, 21.5f)
            verticalLineTo(23.0f)
            lineTo(21.25f, 20.75f)
            lineTo(19.0f, 18.5f)
            verticalLineTo(20.0f)
            close()
        }
    }.build()
}
