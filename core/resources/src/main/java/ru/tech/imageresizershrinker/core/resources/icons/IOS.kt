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

val Icons.Rounded.IOS: ImageVector by lazy {
    Builder(
        name = "IOS", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(18.71f, 19.5f)
            curveTo(17.88f, 20.74f, 17.0f, 21.95f, 15.66f, 21.97f)
            curveTo(14.32f, 22.0f, 13.89f, 21.18f, 12.37f, 21.18f)
            curveTo(10.84f, 21.18f, 10.37f, 21.95f, 9.1f, 22.0f)
            curveTo(7.79f, 22.05f, 6.8f, 20.68f, 5.96f, 19.47f)
            curveTo(4.25f, 17.0f, 2.94f, 12.45f, 4.7f, 9.39f)
            curveTo(5.57f, 7.87f, 7.13f, 6.91f, 8.82f, 6.88f)
            curveTo(10.1f, 6.86f, 11.32f, 7.75f, 12.11f, 7.75f)
            curveTo(12.89f, 7.75f, 14.37f, 6.68f, 15.92f, 6.84f)
            curveTo(16.57f, 6.87f, 18.39f, 7.1f, 19.56f, 8.82f)
            curveTo(19.47f, 8.88f, 17.39f, 10.1f, 17.41f, 12.63f)
            curveTo(17.44f, 15.65f, 20.06f, 16.66f, 20.09f, 16.67f)
            curveTo(20.06f, 16.74f, 19.67f, 18.11f, 18.71f, 19.5f)
            moveTo(13.0f, 3.5f)
            curveTo(13.73f, 2.67f, 14.94f, 2.04f, 15.94f, 2.0f)
            curveTo(16.07f, 3.17f, 15.6f, 4.35f, 14.9f, 5.19f)
            curveTo(14.21f, 6.04f, 13.07f, 6.7f, 11.95f, 6.61f)
            curveTo(11.8f, 5.46f, 12.36f, 4.26f, 13.0f, 3.5f)
            close()
        }
    }.build()
}