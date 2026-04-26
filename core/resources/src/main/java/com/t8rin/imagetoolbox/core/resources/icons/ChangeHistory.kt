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

val Icons.Outlined.ChangeHistory: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ChangeHistory",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(152f, 800f)
            quadToRelative(-23f, 0f, -35f, -20.5f)
            reflectiveQuadToRelative(1f, -40.5f)
            lineToRelative(328f, -525f)
            quadToRelative(12f, -19f, 34f, -19f)
            reflectiveQuadToRelative(34f, 19f)
            lineToRelative(328f, 525f)
            quadToRelative(13f, 20f, 1f, 40.5f)
            reflectiveQuadTo(808f, 800f)
            lineTo(152f, 800f)
            close()
            moveTo(224f, 720f)
            horizontalLineToRelative(512f)
            lineTo(480f, 310f)
            lineTo(224f, 720f)
            close()
            moveTo(480f, 515f)
            close()
        }
    }.build()
}
