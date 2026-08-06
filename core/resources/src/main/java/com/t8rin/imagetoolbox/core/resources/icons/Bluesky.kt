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

val Icons.Rounded.Bluesky: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Bluesky",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(5.202f, 2.857f)
            curveTo(7.954f, 4.922f, 10.913f, 9.11f, 12f, 11.358f)
            curveToRelative(1.087f, -2.247f, 4.046f, -6.436f, 6.798f, -8.501f)
            curveTo(20.783f, 1.366f, 24f, 0.213f, 24f, 3.883f)
            curveToRelative(0f, 0.732f, -0.42f, 6.156f, -0.667f, 7.037f)
            curveToRelative(-0.856f, 3.061f, -3.978f, 3.842f, -6.755f, 3.37f)
            curveToRelative(4.854f, 0.826f, 6.089f, 3.562f, 3.422f, 6.299f)
            curveToRelative(-5.065f, 5.196f, -7.28f, -1.304f, -7.847f, -2.97f)
            curveToRelative(-0.104f, -0.305f, -0.152f, -0.448f, -0.153f, -0.327f)
            curveToRelative(0f, -0.121f, -0.05f, 0.022f, -0.153f, 0.327f)
            curveToRelative(-0.568f, 1.666f, -2.782f, 8.166f, -7.847f, 2.97f)
            curveToRelative(-2.667f, -2.737f, -1.432f, -5.473f, 3.422f, -6.3f)
            curveToRelative(-2.777f, 0.473f, -5.899f, -0.308f, -6.755f, -3.369f)
            curveTo(0.42f, 10.04f, 0f, 4.615f, 0f, 3.883f)
            curveToRelative(0f, -3.67f, 3.217f, -2.517f, 5.202f, -1.026f)
        }
    }.build()
}
