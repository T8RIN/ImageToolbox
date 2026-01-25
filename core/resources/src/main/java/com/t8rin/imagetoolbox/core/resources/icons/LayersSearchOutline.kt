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

val Icons.Outlined.LayersSearchOutline: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.LayersSearchOutline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.31f, 18.9f)
            curveTo(19.75f, 18.21f, 20f, 17.38f, 20f, 16.5f)
            curveTo(20f, 14f, 18f, 12f, 15.5f, 12f)
            reflectiveCurveTo(11f, 14f, 11f, 16.5f)
            reflectiveCurveTo(13f, 21f, 15.5f, 21f)
            curveTo(16.37f, 21f, 17.19f, 20.75f, 17.88f, 20.32f)
            lineTo(21f, 23.39f)
            lineTo(22.39f, 22f)
            lineTo(19.31f, 18.9f)
            moveTo(15.5f, 19f)
            curveTo(14.12f, 19f, 13f, 17.88f, 13f, 16.5f)
            reflectiveCurveTo(14.12f, 14f, 15.5f, 14f)
            reflectiveCurveTo(18f, 15.12f, 18f, 16.5f)
            reflectiveCurveTo(16.88f, 19f, 15.5f, 19f)
            moveTo(9.59f, 19.2f)
            lineTo(3f, 14.07f)
            lineTo(4.62f, 12.81f)
            lineTo(9f, 16.22f)
            curveTo(9f, 16.32f, 9f, 16.41f, 9f, 16.5f)
            curveTo(9f, 17.46f, 9.22f, 18.38f, 9.59f, 19.2f)
            moveTo(9.5f, 14.04f)
            lineTo(3f, 9f)
            lineTo(12f, 2f)
            lineTo(21f, 9f)
            lineTo(18.66f, 10.82f)
            curveTo(17.96f, 10.44f, 17.19f, 10.18f, 16.37f, 10.07f)
            lineTo(17.74f, 9f)
            lineTo(12f, 4.53f)
            lineTo(6.26f, 9f)
            lineTo(10.53f, 12.32f)
            curveTo(10.1f, 12.84f, 9.74f, 13.42f, 9.5f, 14.04f)
            close()
        }
    }.build()
}
