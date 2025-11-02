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

package com.t8rin.imagetoolbox.core.resources.icons

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

val Icons.Outlined.Cool: ImageVector by lazy {
    Builder(
        name = "Outlined.Cool", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(19.0f, 10.0f)
            curveTo(19.0f, 11.38f, 16.88f, 12.5f, 15.5f, 12.5f)
            curveTo(14.12f, 12.5f, 12.75f, 11.38f, 12.75f, 10.0f)
            horizontalLineTo(11.25f)
            curveTo(11.25f, 11.38f, 9.88f, 12.5f, 8.5f, 12.5f)
            curveTo(7.12f, 12.5f, 5.0f, 11.38f, 5.0f, 10.0f)
            horizontalLineTo(4.25f)
            curveTo(4.09f, 10.64f, 4.0f, 11.31f, 4.0f, 12.0f)
            arcTo(
                8.0f, 8.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 12.0f,
                y1 = 20.0f
            )
            arcTo(
                8.0f, 8.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 20.0f,
                y1 = 12.0f
            )
            curveTo(20.0f, 11.31f, 19.91f, 10.64f, 19.75f, 10.0f)
            horizontalLineTo(19.0f)
            moveTo(12.0f, 4.0f)
            curveTo(9.04f, 4.0f, 6.45f, 5.61f, 5.07f, 8.0f)
            horizontalLineTo(18.93f)
            curveTo(17.55f, 5.61f, 14.96f, 4.0f, 12.0f, 4.0f)
            moveTo(22.0f, 12.0f)
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 12.0f,
                y1 = 22.0f
            )
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 2.0f,
                y1 = 12.0f
            )
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 12.0f,
                y1 = 2.0f
            )
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 22.0f,
                y1 = 12.0f
            )
            moveTo(12.0f, 17.23f)
            curveTo(10.25f, 17.23f, 8.71f, 16.5f, 7.81f, 15.42f)
            lineTo(9.23f, 14.0f)
            curveTo(9.68f, 14.72f, 10.75f, 15.23f, 12.0f, 15.23f)
            curveTo(13.25f, 15.23f, 14.32f, 14.72f, 14.77f, 14.0f)
            lineTo(16.19f, 15.42f)
            curveTo(15.29f, 16.5f, 13.75f, 17.23f, 12.0f, 17.23f)
            close()
        }
    }.build()
}

val Icons.Rounded.Cool: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    Builder(
        name = "Rounded.Cool",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(3.22f, 7.22f)
            curveTo(4.91f, 4.11f, 8.21f, 2f, 12f, 2f)
            curveTo(15.79f, 2f, 19.09f, 4.11f, 20.78f, 7.22f)
            lineTo(20f, 8f)
            horizontalLineTo(4f)
            lineTo(3.22f, 7.22f)
            moveTo(21.4f, 8.6f)
            curveTo(21.78f, 9.67f, 22f, 10.81f, 22f, 12f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 22f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 12f)
            curveTo(2f, 10.81f, 2.22f, 9.67f, 2.6f, 8.6f)
            lineTo(4f, 10f)
            horizontalLineTo(5f)
            curveTo(5f, 11.38f, 7.12f, 12.5f, 8.5f, 12.5f)
            curveTo(9.88f, 12.5f, 11.25f, 11.38f, 11.25f, 10f)
            horizontalLineTo(12.75f)
            curveTo(12.75f, 11.38f, 14.12f, 12.5f, 15.5f, 12.5f)
            curveTo(16.88f, 12.5f, 19f, 11.38f, 19f, 10f)
            horizontalLineTo(20f)
            lineTo(21.4f, 8.6f)
            moveTo(16.19f, 15.42f)
            lineTo(14.77f, 14f)
            curveTo(14.32f, 14.72f, 13.25f, 15.23f, 12f, 15.23f)
            curveTo(10.75f, 15.23f, 9.68f, 14.72f, 9.23f, 14f)
            lineTo(7.81f, 15.42f)
            curveTo(8.71f, 16.5f, 10.25f, 17.23f, 12f, 17.23f)
            curveTo(13.75f, 17.23f, 15.29f, 16.5f, 16.19f, 15.42f)
            close()
        }
    }.build()
}