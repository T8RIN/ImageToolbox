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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Done: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Done",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(381f, 720f)
            lineToRelative(424f, -424f)
            lineToRelative(-57f, -56f)
            lineToRelative(-368f, 367f)
            lineToRelative(-169f, -170f)
            lineToRelative(-57f, 57f)
            lineToRelative(227f, 226f)
            close()
            moveTo(324f, 776f)
            lineTo(98f, 550f)
            quadToRelative(-12f, -12f, -17.5f, -26.5f)
            reflectiveQuadTo(75f, 494f)
            quadToRelative(0f, -15f, 5.5f, -30f)
            reflectiveQuadTo(98f, 437f)
            lineToRelative(56f, -56f)
            quadToRelative(12f, -12f, 26.5f, -18f)
            reflectiveQuadToRelative(30.5f, -6f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(26.5f, 18f)
            lineToRelative(113f, 113f)
            lineToRelative(310f, -311f)
            quadToRelative(11f, -12f, 26f, -17.5f)
            reflectiveQuadToRelative(30f, -5.5f)
            quadToRelative(15f, 0f, 30f, 5.5f)
            reflectiveQuadToRelative(27f, 16.5f)
            lineToRelative(57f, 56f)
            quadToRelative(12f, 12f, 18f, 26.5f)
            reflectiveQuadToRelative(6f, 30.5f)
            quadToRelative(0f, 16f, -5.5f, 30.5f)
            reflectiveQuadTo(862f, 352f)
            lineTo(438f, 776f)
            quadToRelative(-12f, 12f, -27f, 18f)
            reflectiveQuadToRelative(-30f, 6f)
            quadToRelative(-15f, 0f, -30f, -6f)
            reflectiveQuadToRelative(-27f, -18f)
            close()
        }
    }.build()
}
