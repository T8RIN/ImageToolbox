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

val Icons.Rounded.Lasso: ImageVector by lazy {
    Builder(
        name = "Lasso", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.0f, 2.0f)
            curveTo(17.5f, 2.0f, 22.0f, 5.13f, 22.0f, 9.0f)
            curveTo(22.0f, 12.26f, 18.81f, 15.0f, 14.5f, 15.78f)
            lineTo(14.5f, 15.5f)
            curveTo(14.5f, 14.91f, 14.4f, 14.34f, 14.21f, 13.81f)
            curveTo(17.55f, 13.21f, 20.0f, 11.28f, 20.0f, 9.0f)
            curveTo(20.0f, 6.24f, 16.42f, 4.0f, 12.0f, 4.0f)
            curveTo(7.58f, 4.0f, 4.0f, 6.24f, 4.0f, 9.0f)
            curveTo(4.0f, 10.19f, 4.67f, 11.29f, 5.79f, 12.15f)
            curveTo(5.35f, 12.64f, 5.0f, 13.21f, 4.78f, 13.85f)
            curveTo(3.06f, 12.59f, 2.0f, 10.88f, 2.0f, 9.0f)
            curveTo(2.0f, 5.13f, 6.5f, 2.0f, 12.0f, 2.0f)
            moveTo(9.5f, 12.0f)
            curveTo(11.43f, 12.0f, 13.0f, 13.57f, 13.0f, 15.5f)
            curveTo(13.0f, 17.4f, 11.5f, 18.95f, 9.6f, 19.0f)
            curveTo(9.39f, 19.36f, 9.18f, 20.0f, 9.83f, 20.68f)
            curveTo(11.0f, 21.88f, 13.28f, 19.72f, 16.39f, 19.71f)
            curveTo(18.43f, 19.7f, 20.03f, 19.97f, 20.03f, 19.97f)
            curveTo(20.03f, 19.97f, 21.08f, 20.1f, 20.97f, 21.04f)
            curveTo(20.86f, 21.97f, 19.91f, 21.97f, 19.91f, 21.97f)
            curveTo(19.53f, 21.93f, 18.03f, 21.58f, 16.22f, 21.68f)
            curveTo(14.41f, 21.77f, 13.47f, 22.41f, 12.56f, 22.69f)
            curveTo(11.66f, 22.97f, 9.91f, 23.38f, 8.3f, 22.05f)
            curveTo(6.97f, 20.96f, 7.46f, 19.11f, 7.67f, 18.5f)
            curveTo(6.67f, 17.87f, 6.0f, 16.76f, 6.0f, 15.5f)
            curveTo(6.0f, 13.57f, 7.57f, 12.0f, 9.5f, 12.0f)
            moveTo(9.5f, 14.0f)
            curveTo(8.67f, 14.0f, 8.0f, 14.67f, 8.0f, 15.5f)
            curveTo(8.0f, 16.33f, 8.67f, 17.0f, 9.5f, 17.0f)
            curveTo(10.33f, 17.0f, 11.0f, 16.33f, 11.0f, 15.5f)
            curveTo(11.0f, 14.67f, 10.33f, 14.0f, 9.5f, 14.0f)
            close()
        }
    }.build()
}