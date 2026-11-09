/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.Snake: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Snake",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.5f, 17f)
            horizontalLineTo(18f)
            curveTo(19.1f, 17f, 20f, 16.1f, 20f, 15f)
            reflectiveCurveTo(19.1f, 13f, 18f, 13f)
            verticalLineTo(9f)
            curveTo(18f, 8f, 18f, 7f, 16.92f, 6.14f)
            curveTo(16.97f, 5.93f, 17f, 5.72f, 17f, 5.5f)
            curveTo(17f, 3.57f, 15f, 2f, 12.5f, 2f)
            curveTo(10.24f, 2f, 8.38f, 3.31f, 8.07f, 5f)
            horizontalLineTo(6f)
            lineTo(3.71f, 2.79f)
            lineTo(3f, 3.5f)
            lineTo(5f, 5.5f)
            lineTo(3f, 7.5f)
            lineTo(3.71f, 8.21f)
            lineTo(6f, 6f)
            horizontalLineTo(8.07f)
            curveTo(8.38f, 7.69f, 10.24f, 9f, 12.5f, 9f)
            curveTo(13f, 9f, 13.5f, 8.92f, 13.93f, 8.8f)
            curveTo(13.97f, 8.87f, 14f, 8.94f, 14f, 9f)
            verticalLineTo(13f)
            horizontalLineTo(8f)
            curveTo(6.9f, 13f, 6f, 13.9f, 6f, 15f)
            reflectiveCurveTo(6.9f, 17f, 8f, 17f)
            horizontalLineTo(6.5f)
            curveTo(5.12f, 17f, 4f, 18.12f, 4f, 19.5f)
            curveTo(4f, 19.67f, 4f, 19.84f, 4.05f, 20f)
            horizontalLineTo(4f)
            curveTo(2.9f, 20f, 2f, 20.9f, 2f, 22f)
            horizontalLineTo(19.5f)
            curveTo(20.88f, 22f, 22f, 20.88f, 22f, 19.5f)
            reflectiveCurveTo(20.88f, 17f, 19.5f, 17f)
            moveTo(12f, 5f)
            curveTo(11.45f, 5f, 11f, 4.55f, 11f, 4f)
            reflectiveCurveTo(11.45f, 3f, 12f, 3f)
            reflectiveCurveTo(13f, 3.45f, 13f, 4f)
            reflectiveCurveTo(12.55f, 5f, 12f, 5f)
            close()
        }
    }.build()
}