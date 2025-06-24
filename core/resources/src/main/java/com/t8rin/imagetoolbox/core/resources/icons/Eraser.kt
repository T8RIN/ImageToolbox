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

val Icons.Rounded.Eraser: ImageVector by lazy {
    Builder(
        name = "Eraser", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(16.24f, 3.56f)
            lineTo(21.19f, 8.5f)
            curveTo(21.97f, 9.29f, 21.97f, 10.55f, 21.19f, 11.34f)
            lineTo(12.0f, 20.53f)
            curveTo(10.44f, 22.09f, 7.91f, 22.09f, 6.34f, 20.53f)
            lineTo(2.81f, 17.0f)
            curveTo(2.03f, 16.21f, 2.03f, 14.95f, 2.81f, 14.16f)
            lineTo(13.41f, 3.56f)
            curveTo(14.2f, 2.78f, 15.46f, 2.78f, 16.24f, 3.56f)
            moveTo(4.22f, 15.58f)
            lineTo(7.76f, 19.11f)
            curveTo(8.54f, 19.9f, 9.8f, 19.9f, 10.59f, 19.11f)
            lineTo(14.12f, 15.58f)
            lineTo(9.17f, 10.63f)
            lineTo(4.22f, 15.58f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Eraser: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    Builder(
        name = "TwoTone.Eraser",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(4.22f, 15.58f)
            lineToRelative(3.54f, 3.53f)
            curveToRelative(0.78f, 0.79f, 2.04f, 0.79f, 2.83f, 0f)
            lineToRelative(3.53f, -3.53f)
            lineToRelative(-4.95f, -4.95f)
            lineToRelative(-4.95f, 4.95f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(21.19f, 8.5f)
            lineToRelative(-4.95f, -4.94f)
            curveToRelative(-0.78f, -0.78f, -2.04f, -0.78f, -2.83f, 0f)
            lineTo(2.81f, 14.16f)
            curveToRelative(-0.78f, 0.79f, -0.78f, 2.05f, 0f, 2.84f)
            lineToRelative(3.53f, 3.53f)
            curveToRelative(1.57f, 1.56f, 4.1f, 1.56f, 5.66f, 0f)
            lineToRelative(9.19f, -9.19f)
            curveToRelative(0.78f, -0.79f, 0.78f, -2.05f, 0f, -2.84f)
            close()
            moveTo(10.59f, 19.11f)
            curveToRelative(-0.79f, 0.79f, -2.05f, 0.79f, -2.83f, 0f)
            lineToRelative(-3.54f, -3.53f)
            lineToRelative(4.95f, -4.95f)
            lineToRelative(4.95f, 4.95f)
            lineToRelative(-3.53f, 3.53f)
            close()
        }
    }.build()
}