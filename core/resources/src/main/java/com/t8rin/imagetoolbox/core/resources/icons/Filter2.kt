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

val Icons.Outlined.Filter2: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Filter2",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(13f, 13f)
            lineTo(13f, 11f)
            horizontalLineToRelative(2f)
            quadToRelative(0.83f, 0f, 1.41f, -0.59f)
            reflectiveQuadTo(17f, 9f)
            lineTo(17f, 7f)
            quadToRelative(0f, -0.82f, -0.59f, -1.41f)
            reflectiveQuadTo(15f, 5f)
            lineTo(12f, 5f)
            quadToRelative(-0.42f, 0f, -0.71f, 0.29f)
            reflectiveQuadTo(11f, 6f)
            reflectiveQuadToRelative(0.29f, 0.71f)
            reflectiveQuadTo(12f, 7f)
            horizontalLineToRelative(3f)
            lineTo(15f, 9f)
            lineTo(13f, 9f)
            quadToRelative(-0.82f, 0f, -1.41f, 0.59f)
            reflectiveQuadTo(11f, 11f)
            verticalLineToRelative(3f)
            quadToRelative(0f, 0.42f, 0.29f, 0.71f)
            reflectiveQuadTo(12f, 15f)
            horizontalLineToRelative(4f)
            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
            reflectiveQuadTo(17f, 14f)
            reflectiveQuadToRelative(-0.29f, -0.71f)
            reflectiveQuadTo(16f, 13f)
            lineTo(13f, 13f)
            close()
            moveTo(8f, 18f)
            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
            reflectiveQuadTo(6f, 16f)
            lineTo(6f, 4f)
            quadTo(6f, 3.17f, 6.59f, 2.59f)
            reflectiveQuadTo(8f, 2f)
            lineTo(20f, 2f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(22f, 4f)
            lineTo(22f, 16f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(20f, 18f)
            lineTo(8f, 18f)
            close()
            moveTo(8f, 16f)
            lineTo(20f, 16f)
            lineTo(20f, 4f)
            lineTo(8f, 4f)
            lineTo(8f, 16f)
            close()
            moveTo(4f, 22f)
            quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
            reflectiveQuadTo(2f, 20f)
            lineTo(2f, 7f)
            quadTo(2f, 6.57f, 2.29f, 6.29f)
            reflectiveQuadTo(3f, 6f)
            reflectiveQuadTo(3.71f, 6.29f)
            reflectiveQuadTo(4f, 7f)
            lineTo(4f, 20f)
            lineTo(17f, 20f)
            quadToRelative(0.43f, 0f, 0.71f, 0.29f)
            reflectiveQuadTo(18f, 21f)
            reflectiveQuadToRelative(-0.29f, 0.71f)
            reflectiveQuadTo(17f, 22f)
            lineTo(4f, 22f)
            close()
            moveTo(8f, 4f)
            lineTo(8f, 16f)
            lineTo(8f, 4f)
            close()
        }
    }.build()
}