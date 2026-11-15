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

val Icons.Outlined.Ufo: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Ufo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(17f, 10.54f)
            curveTo(16.78f, 7.44f, 14.63f, 5f, 12f, 5f)
            reflectiveCurveTo(7.22f, 7.44f, 7f, 10.54f)
            curveTo(4f, 11.23f, 2f, 12.5f, 2f, 14f)
            curveTo(2f, 16.21f, 6.5f, 18f, 12f, 18f)
            reflectiveCurveTo(22f, 16.21f, 22f, 14f)
            curveTo(22f, 12.5f, 20f, 11.23f, 17f, 10.54f)
            moveTo(14.93f, 11.84f)
            curveTo(13.03f, 12.05f, 10.97f, 12.05f, 9.07f, 11.84f)
            curveTo(9.03f, 11.56f, 9f, 11.28f, 9f, 11f)
            curveTo(9f, 8.8f, 10.35f, 7f, 12f, 7f)
            reflectiveCurveTo(15f, 8.8f, 15f, 11f)
            curveTo(15f, 11.28f, 15f, 11.56f, 14.93f, 11.84f)
            close()
        }
    }.build()
}
