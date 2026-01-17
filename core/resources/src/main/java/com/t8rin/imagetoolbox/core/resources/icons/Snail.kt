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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Snail: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Snail",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.31f, 8.03f)
            lineTo(21.24f, 4.95f)
            curveTo(21.67f, 4.85f, 22f, 4.47f, 22f, 4f)
            curveTo(22f, 3.45f, 21.55f, 3f, 21f, 3f)
            reflectiveCurveTo(20f, 3.45f, 20f, 4f)
            curveTo(20f, 4.26f, 20.11f, 4.5f, 20.27f, 4.68f)
            lineTo(19.5f, 7.26f)
            lineTo(18.73f, 4.68f)
            curveTo(18.89f, 4.5f, 19f, 4.26f, 19f, 4f)
            curveTo(19f, 3.45f, 18.55f, 3f, 18f, 3f)
            reflectiveCurveTo(17f, 3.45f, 17f, 4f)
            curveTo(17f, 4.47f, 17.33f, 4.85f, 17.76f, 4.95f)
            lineTo(18.69f, 8.03f)
            curveTo(17.73f, 8.18f, 17f, 9f, 17f, 10f)
            verticalLineTo(12.25f)
            curveTo(15.65f, 9.16f, 12.63f, 7f, 9.11f, 7f)
            curveTo(5.19f, 7f, 2f, 10.26f, 2f, 14.26f)
            curveTo(2f, 16.1f, 2.82f, 17.75f, 4.1f, 18.85f)
            lineTo(2.88f, 19f)
            curveTo(2.38f, 19.06f, 2f, 19.5f, 2f, 20f)
            curveTo(2f, 20.55f, 2.45f, 21f, 3f, 21f)
            lineTo(19.12f, 21f)
            curveTo(20.16f, 21f, 21f, 20.16f, 21f, 19.12f)
            verticalLineTo(11.72f)
            curveTo(21.6f, 11.38f, 22f, 10.74f, 22f, 10f)
            curveTo(22f, 9f, 21.27f, 8.18f, 20.31f, 8.03f)
            moveTo(15.6f, 17.41f)
            lineTo(12.07f, 17.86f)
            curveTo(12.5f, 17.1f, 12.8f, 16.21f, 12.8f, 15.26f)
            curveTo(12.8f, 12.94f, 10.95f, 11.06f, 8.67f, 11.06f)
            curveTo(8.14f, 11.06f, 7.62f, 11.18f, 7.14f, 11.41f)
            curveTo(6.65f, 11.66f, 6.44f, 12.26f, 6.69f, 12.75f)
            curveTo(6.93f, 13.25f, 7.53f, 13.45f, 8.03f, 13.21f)
            curveTo(8.23f, 13.11f, 8.45f, 13.06f, 8.67f, 13.06f)
            curveTo(9.85f, 13.06f, 10.8f, 14.04f, 10.8f, 15.26f)
            curveTo(10.8f, 16.92f, 9.5f, 18.27f, 7.89f, 18.27f)
            curveTo(5.75f, 18.27f, 4f, 16.47f, 4f, 14.26f)
            curveTo(4f, 11.36f, 6.29f, 9f, 9.11f, 9f)
            curveTo(12.77f, 9f, 15.75f, 12.06f, 15.75f, 15.82f)
            curveTo(15.75f, 16.36f, 15.69f, 16.89f, 15.6f, 17.41f)
            close()
        }
    }.build()
}
