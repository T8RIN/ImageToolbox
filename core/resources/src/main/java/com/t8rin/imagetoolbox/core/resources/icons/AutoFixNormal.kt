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

val Icons.Rounded.AutoFixNormal: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.AutoFixNormal",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(19.05f, 4.95f)
            lineToRelative(-1.075f, -0.5f)
            quadTo(17.7f, 4.325f, 17.7f, 4f)
            reflectiveQuadToRelative(0.275f, -0.45f)
            lineToRelative(1.075f, -0.5f)
            lineToRelative(0.5f, -1.075f)
            quadToRelative(0.125f, -0.275f, 0.45f, -0.275f)
            reflectiveQuadToRelative(0.45f, 0.275f)
            lineToRelative(0.5f, 1.075f)
            lineToRelative(1.075f, 0.5f)
            quadToRelative(0.275f, 0.125f, 0.275f, 0.45f)
            reflectiveQuadToRelative(-0.275f, 0.45f)
            lineToRelative(-1.075f, 0.5f)
            lineToRelative(-0.5f, 1.075f)
            quadToRelative(-0.125f, 0.275f, -0.45f, 0.275f)
            reflectiveQuadToRelative(-0.45f, -0.275f)
            close()
            moveTo(5.1f, 21.7f)
            lineToRelative(-2.8f, -2.8f)
            quadToRelative(-0.3f, -0.3f, -0.3f, -0.725f)
            reflectiveQuadToRelative(0.3f, -0.725f)
            lineTo(13.45f, 6.3f)
            quadToRelative(0.3f, -0.3f, 0.725f, -0.3f)
            reflectiveQuadToRelative(0.725f, 0.3f)
            lineToRelative(2.8f, 2.8f)
            quadToRelative(0.3f, 0.3f, 0.3f, 0.725f)
            reflectiveQuadToRelative(-0.3f, 0.725f)
            lineTo(6.55f, 21.7f)
            quadToRelative(-0.3f, 0.3f, -0.725f, 0.3f)
            reflectiveQuadToRelative(-0.725f, -0.3f)
            close()
            moveTo(14.175f, 11.225f)
            lineTo(15.575f, 9.825f)
            lineTo(14.175f, 8.425f)
            lineTo(12.775f, 9.825f)
            close()
        }
    }.build()
}
