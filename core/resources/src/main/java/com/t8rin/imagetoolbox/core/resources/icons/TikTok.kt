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

val Icons.Rounded.TikTok: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.TikTok",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12.525f, 0.02f)
            curveToRelative(1.31f, -0.02f, 2.61f, -0.01f, 3.91f, -0.02f)
            curveToRelative(0.08f, 1.53f, 0.63f, 3.09f, 1.75f, 4.17f)
            curveToRelative(1.12f, 1.11f, 2.7f, 1.62f, 4.24f, 1.79f)
            verticalLineToRelative(4.03f)
            curveToRelative(-1.44f, -0.05f, -2.89f, -0.35f, -4.2f, -0.97f)
            curveToRelative(-0.57f, -0.26f, -1.1f, -0.59f, -1.62f, -0.93f)
            curveToRelative(-0.01f, 2.92f, 0.01f, 5.84f, -0.02f, 8.75f)
            curveToRelative(-0.08f, 1.4f, -0.54f, 2.79f, -1.35f, 3.94f)
            curveToRelative(-1.31f, 1.92f, -3.58f, 3.17f, -5.91f, 3.21f)
            curveToRelative(-1.43f, 0.08f, -2.86f, -0.31f, -4.08f, -1.03f)
            curveToRelative(-2.02f, -1.19f, -3.44f, -3.37f, -3.65f, -5.71f)
            curveToRelative(-0.02f, -0.5f, -0.03f, -1f, -0.01f, -1.49f)
            curveToRelative(0.18f, -1.9f, 1.12f, -3.72f, 2.58f, -4.96f)
            curveToRelative(1.66f, -1.44f, 3.98f, -2.13f, 6.15f, -1.72f)
            curveToRelative(0.02f, 1.48f, -0.04f, 2.96f, -0.04f, 4.44f)
            curveToRelative(-0.99f, -0.32f, -2.15f, -0.23f, -3.02f, 0.37f)
            curveToRelative(-0.63f, 0.41f, -1.11f, 1.04f, -1.36f, 1.75f)
            curveToRelative(-0.21f, 0.51f, -0.15f, 1.07f, -0.14f, 1.61f)
            curveToRelative(0.24f, 1.64f, 1.82f, 3.02f, 3.5f, 2.87f)
            curveToRelative(1.12f, -0.01f, 2.19f, -0.66f, 2.77f, -1.61f)
            curveToRelative(0.19f, -0.33f, 0.4f, -0.67f, 0.41f, -1.06f)
            curveToRelative(0.1f, -1.79f, 0.06f, -3.57f, 0.07f, -5.36f)
            curveToRelative(0.01f, -4.03f, -0.01f, -8.05f, 0.02f, -12.07f)
            close()
        }
    }.build()
}
