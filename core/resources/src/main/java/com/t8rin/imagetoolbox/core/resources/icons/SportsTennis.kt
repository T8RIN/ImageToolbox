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

val Icons.Outlined.SportsTennis: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SportsTennis",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(13f, 14.55f)
            quadToRelative(-2.42f, 0f, -3.55f, 0.28f)
            reflectiveQuadTo(7.53f, 15.9f)
            lineToRelative(-3.4f, 3.4f)
            quadToRelative(-0.27f, 0.27f, -0.69f, 0.27f)
            reflectiveQuadTo(2.73f, 19.3f)
            quadToRelative(-0.3f, -0.3f, -0.3f, -0.71f)
            quadToRelative(0f, -0.41f, 0.3f, -0.71f)
            lineTo(6.1f, 14.5f)
            quadToRelative(0.77f, -0.78f, 1.06f, -1.94f)
            reflectiveQuadTo(7.45f, 9f)
            quadToRelative(0f, -1.45f, 0.65f, -2.85f)
            reflectiveQuadToRelative(1.85f, -2.6f)
            quadToRelative(2.28f, -2.27f, 5.03f, -2.57f)
            reflectiveQuadTo(19.5f, 2.5f)
            quadTo(21.3f, 4.3f, 21f, 7.05f)
            reflectiveQuadToRelative(-2.55f, 5f)
            quadToRelative(-1.2f, 1.2f, -2.6f, 1.85f)
            reflectiveQuadTo(13f, 14.55f)
            close()
            moveTo(10.3f, 11.65f)
            quadToRelative(1.18f, 1.15f, 3.18f, 0.85f)
            reflectiveQuadToRelative(3.57f, -1.88f)
            quadToRelative(1.6f, -1.6f, 1.91f, -3.59f)
            reflectiveQuadTo(18.1f, 3.92f)
            quadToRelative(-1.2f, -1.2f, -3.14f, -0.9f)
            quadToRelative(-1.94f, 0.3f, -3.56f, 1.9f)
            quadTo(9.83f, 6.5f, 9.49f, 8.49f)
            quadToRelative(-0.34f, 1.99f, 0.81f, 3.16f)
            close()
            moveTo(15.18f, 21.83f)
            quadTo(14f, 20.65f, 14f, 19f)
            reflectiveQuadToRelative(1.18f, -2.82f)
            reflectiveQuadTo(18f, 15f)
            reflectiveQuadToRelative(2.83f, 1.18f)
            reflectiveQuadTo(22f, 19f)
            reflectiveQuadToRelative(-1.17f, 2.82f)
            reflectiveQuadTo(18f, 23f)
            reflectiveQuadToRelative(-2.82f, -1.18f)
            close()
            moveTo(19.42f, 20.42f)
            quadTo(20f, 19.83f, 20f, 19f)
            reflectiveQuadToRelative(-0.59f, -1.41f)
            reflectiveQuadTo(18f, 17f)
            reflectiveQuadToRelative(-1.41f, 0.59f)
            quadTo(16f, 18.18f, 16f, 19f)
            reflectiveQuadToRelative(0.59f, 1.41f)
            reflectiveQuadTo(18f, 21f)
            reflectiveQuadToRelative(1.41f, -0.59f)
            close()
            moveTo(18f, 19f)
            close()
        }
    }.build()
}