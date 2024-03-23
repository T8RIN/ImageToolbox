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

val Icons.Rounded.FingerprintOff: ImageVector by lazy {
    Builder(
        name = "Fingerprint Off", defaultWidth = 24.0.dp, defaultHeight
        = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(1.5f, 4.77f)
            lineTo(2.78f, 3.5f)
            lineTo(20.5f, 21.22f)
            lineTo(19.23f, 22.5f)
            lineTo(16.67f, 19.94f)
            curveTo(15.58f, 19.9f, 14.62f, 19.6f, 13.82f, 19.05f)
            curveTo(12.34f, 18.05f, 11.46f, 16.43f, 11.44f, 14.71f)
            lineTo(10.27f, 13.53f)
            curveTo(10.03f, 13.85f, 9.89f, 14.23f, 9.89f, 14.65f)
            curveTo(9.89f, 16.36f, 10.55f, 17.96f, 11.76f, 19.16f)
            curveTo(12.71f, 20.1f, 13.62f, 20.62f, 15.03f, 21.0f)
            curveTo(15.3f, 21.08f, 15.45f, 21.36f, 15.38f, 21.62f)
            curveTo(15.33f, 21.85f, 15.12f, 22.0f, 14.91f, 22.0f)
            horizontalLineTo(14.78f)
            curveTo(13.19f, 21.54f, 12.15f, 20.95f, 11.06f, 19.88f)
            curveTo(9.66f, 18.5f, 8.89f, 16.64f, 8.89f, 14.66f)
            curveTo(8.89f, 13.97f, 9.14f, 13.33f, 9.56f, 12.83f)
            lineTo(8.5f, 11.77f)
            curveTo(7.78f, 12.54f, 7.34f, 13.55f, 7.34f, 14.66f)
            curveTo(7.34f, 16.1f, 7.66f, 17.43f, 8.27f, 18.5f)
            curveTo(8.91f, 19.66f, 9.35f, 20.15f, 10.12f, 20.93f)
            curveTo(10.31f, 21.13f, 10.31f, 21.44f, 10.12f, 21.64f)
            curveTo(10.0f, 21.74f, 9.88f, 21.79f, 9.75f, 21.79f)
            curveTo(9.62f, 21.79f, 9.5f, 21.74f, 9.4f, 21.64f)
            curveTo(8.53f, 20.77f, 8.06f, 20.21f, 7.39f, 19.0f)
            curveTo(6.7f, 17.77f, 6.34f, 16.27f, 6.34f, 14.66f)
            curveTo(6.34f, 13.28f, 6.89f, 12.0f, 7.79f, 11.06f)
            lineTo(6.7f, 9.97f)
            curveTo(6.15f, 10.5f, 5.69f, 11.15f, 5.35f, 11.86f)
            curveTo(4.96f, 12.67f, 4.76f, 13.62f, 4.76f, 14.66f)
            curveTo(4.76f, 15.44f, 4.83f, 16.67f, 5.43f, 18.27f)
            curveTo(5.53f, 18.53f, 5.4f, 18.82f, 5.14f, 18.91f)
            curveTo(4.88f, 19.0f, 4.59f, 18.87f, 4.5f, 18.62f)
            curveTo(4.0f, 17.31f, 3.77f, 16.0f, 3.77f, 14.66f)
            curveTo(3.77f, 13.46f, 4.0f, 12.37f, 4.45f, 11.42f)
            curveTo(4.84f, 10.61f, 5.36f, 9.88f, 6.0f, 9.26f)
            lineTo(4.97f, 8.24f)
            curveTo(4.58f, 8.63f, 4.22f, 9.05f, 3.89f, 9.5f)
            curveTo(3.81f, 9.65f, 3.66f, 9.72f, 3.5f, 9.72f)
            lineTo(3.21f, 9.63f)
            curveTo(3.0f, 9.47f, 2.93f, 9.16f, 3.09f, 8.93f)
            curveTo(3.45f, 8.43f, 3.84f, 7.96f, 4.27f, 7.53f)
            lineTo(1.5f, 4.77f)
            moveTo(17.81f, 4.47f)
            lineTo(17.58f, 4.41f)
            curveTo(15.66f, 3.42f, 14.0f, 3.0f, 12.0f, 3.0f)
            curveTo(10.03f, 3.0f, 8.15f, 3.47f, 6.44f, 4.41f)
            lineTo(6.29f, 4.46f)
            lineTo(5.71f, 3.89f)
            curveTo(5.73f, 3.74f, 5.82f, 3.61f, 5.96f, 3.53f)
            curveTo(7.82f, 2.5f, 9.86f, 2.0f, 12.0f, 2.0f)
            curveTo(14.14f, 2.0f, 16.0f, 2.47f, 18.04f, 3.5f)
            curveTo(18.29f, 3.65f, 18.38f, 3.95f, 18.25f, 4.19f)
            curveTo(18.16f, 4.37f, 18.0f, 4.47f, 17.81f, 4.47f)
            moveTo(17.15f, 5.65f)
            curveTo(18.65f, 6.42f, 19.91f, 7.5f, 20.9f, 8.9f)
            curveTo(21.06f, 9.12f, 21.0f, 9.44f, 20.78f, 9.6f)
            curveTo(20.55f, 9.76f, 20.24f, 9.71f, 20.08f, 9.5f)
            curveTo(19.18f, 8.22f, 18.04f, 7.23f, 16.69f, 6.54f)
            curveTo(14.06f, 5.19f, 10.76f, 5.08f, 8.03f, 6.21f)
            lineTo(7.27f, 5.45f)
            curveTo(10.34f, 4.04f, 14.14f, 4.1f, 17.15f, 5.65f)
            moveTo(12.0f, 9.27f)
            curveTo(15.12f, 9.27f, 17.66f, 11.69f, 17.66f, 14.66f)
            arcTo(
                0.5f, 0.5f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 17.16f,
                y1 = 15.16f
            )
            lineTo(16.93f, 15.11f)
            lineTo(16.72f, 14.89f)
            lineTo(16.66f, 14.66f)
            curveTo(16.66f, 12.27f, 14.62f, 10.32f, 12.09f, 10.27f)
            lineTo(11.15f, 9.33f)
            lineTo(12.0f, 9.27f)
            moveTo(14.38f, 18.22f)
            curveTo(14.71f, 18.45f, 15.07f, 18.62f, 15.47f, 18.73f)
            lineTo(12.63f, 15.9f)
            curveTo(12.92f, 16.82f, 13.53f, 17.65f, 14.38f, 18.22f)
            moveTo(19.21f, 14.66f)
            curveTo(19.21f, 10.89f, 15.96f, 7.83f, 11.96f, 7.83f)
            curveTo(11.26f, 7.83f, 10.58f, 7.93f, 9.93f, 8.11f)
            lineTo(9.12f, 7.3f)
            curveTo(10.0f, 7.0f, 10.97f, 6.82f, 11.96f, 6.82f)
            curveTo(16.5f, 6.82f, 20.21f, 10.33f, 20.21f, 14.65f)
            curveTo(20.21f, 15.65f, 19.69f, 16.53f, 18.89f, 17.06f)
            lineTo(18.17f, 16.34f)
            curveTo(18.79f, 16.0f, 19.21f, 15.38f, 19.21f, 14.66f)
            close()
        }
    }.build()
}
