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

val Icons.Outlined.Raven: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Raven",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(10f, 18f)
            lineTo(8.73f, 21.08f)
            quadToRelative(-0.15f, 0.38f, -0.54f, 0.54f)
            reflectiveQuadTo(7.43f, 21.63f)
            reflectiveQuadTo(6.89f, 21.09f)
            reflectiveQuadTo(6.88f, 20.33f)
            lineToRelative(1.08f, -2.6f)
            quadToRelative(-2.65f, -0.7f, -4.3f, -2.85f)
            reflectiveQuadTo(2f, 10f)
            lineTo(2f, 6f)
            quadTo(2f, 4.35f, 3.18f, 3.17f)
            reflectiveQuadTo(6f, 2f)
            quadTo(6.55f, 2f, 7.05f, 2.19f)
            reflectiveQuadToRelative(1f, 0.39f)
            lineToRelative(4.78f, 1.95f)
            quadToRelative(0.32f, 0.13f, 0.31f, 0.46f)
            quadTo(13.13f, 5.32f, 12.8f, 5.45f)
            lineTo(10f, 6.5f)
            lineTo(10f, 8f)
            lineToRelative(10.65f, 6.78f)
            quadToRelative(0.18f, 0.13f, 0.27f, 0.29f)
            reflectiveQuadToRelative(0.15f, 0.36f)
            lineToRelative(0.7f, 3.5f)
            quadToRelative(0.07f, 0.43f, -0.19f, 0.75f)
            reflectiveQuadTo(20.9f, 20f)
            lineTo(20.55f, 20f)
            quadToRelative(-0.27f, 0f, -0.47f, -0.14f)
            reflectiveQuadTo(19.75f, 19.5f)
            lineTo(19f, 18f)
            lineTo(14f, 18f)
            verticalLineToRelative(3f)
            quadToRelative(0f, 0.43f, -0.29f, 0.71f)
            reflectiveQuadTo(13f, 22f)
            reflectiveQuadToRelative(-0.71f, -0.29f)
            quadTo(12f, 21.43f, 12f, 21f)
            lineTo(12f, 18f)
            lineTo(10f, 18f)
            close()
            moveTo(5.29f, 6.71f)
            quadTo(5f, 6.43f, 5f, 6f)
            reflectiveQuadTo(5.29f, 5.29f)
            reflectiveQuadTo(6f, 5f)
            reflectiveQuadTo(6.71f, 5.29f)
            reflectiveQuadTo(7f, 6f)
            reflectiveQuadTo(6.71f, 6.71f)
            reflectiveQuadTo(6f, 7f)
            quadTo(5.58f, 7f, 5.29f, 6.71f)
            close()
            moveTo(10f, 15f)
            close()
            moveTo(10f, 16f)
            horizontalLineToRelative(8.82f)
            lineToRelative(-1.57f, -1f)
            lineTo(10f, 15f)
            quadToRelative(-0.9f, 0f, -1.69f, -0.36f)
            reflectiveQuadTo(6.98f, 13.63f)
            quadToRelative(-0.33f, -0.35f, -0.63f, -0.96f)
            reflectiveQuadToRelative(-0.3f, -1.11f)
            quadToRelative(0f, -0.42f, 0.29f, -0.71f)
            reflectiveQuadToRelative(0.71f, -0.29f)
            quadToRelative(0.38f, 0f, 0.65f, 0.25f)
            reflectiveQuadToRelative(0.35f, 0.6f)
            quadToRelative(0.13f, 0.7f, 0.67f, 1.15f)
            quadTo(9.28f, 13f, 10f, 13f)
            horizontalLineToRelative(4.13f)
            lineTo(8f, 9.1f)
            lineTo(8f, 6f)
            quadTo(8f, 5.18f, 7.41f, 4.59f)
            reflectiveQuadTo(6f, 4f)
            reflectiveQuadTo(4.59f, 4.59f)
            quadTo(4f, 5.18f, 4f, 6f)
            verticalLineToRelative(4f)
            quadToRelative(0f, 2.5f, 1.75f, 4.25f)
            reflectiveQuadTo(10f, 16f)
            close()
        }
    }.build()
}