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

val Icons.Rounded.FilterVintage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FilterVintage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(381f, 844f)
            quadToRelative(-44f, -36f, -55f, -92f)
            quadToRelative(-53f, 17f, -107f, -2f)
            reflectiveQuadToRelative(-83f, -66f)
            quadToRelative(-30f, -48f, -22f, -106.5f)
            reflectiveQuadToRelative(52f, -97.5f)
            quadToRelative(-42f, -38f, -50.5f, -94f)
            reflectiveQuadTo(134f, 282f)
            quadToRelative(27f, -48f, 81.5f, -69.5f)
            reflectiveQuadTo(324f, 208f)
            quadToRelative(11f, -56f, 55f, -92f)
            reflectiveQuadToRelative(101f, -36f)
            quadToRelative(57f, 0f, 101f, 36f)
            reflectiveQuadToRelative(55f, 92f)
            quadToRelative(56f, -17f, 108.5f, 3f)
            reflectiveQuadToRelative(81.5f, 71f)
            quadToRelative(27f, 50f, 19.5f, 104.5f)
            reflectiveQuadTo(794f, 480f)
            quadToRelative(44f, 39f, 52.5f, 96.5f)
            reflectiveQuadTo(828f, 684f)
            quadToRelative(-29f, 51f, -81.5f, 68f)
            reflectiveQuadTo(638f, 752f)
            quadToRelative(-11f, 56f, -55f, 92f)
            reflectiveQuadTo(482f, 880f)
            quadToRelative(-57f, 0f, -101f, -36f)
            close()
            moveTo(480f, 640f)
            quadToRelative(66f, 0f, 113f, -47f)
            reflectiveQuadToRelative(47f, -113f)
            quadToRelative(0f, -66f, -47f, -113f)
            reflectiveQuadToRelative(-113f, -47f)
            quadToRelative(-66f, 0f, -113f, 47f)
            reflectiveQuadToRelative(-47f, 113f)
            quadToRelative(0f, 66f, 47f, 113f)
            reflectiveQuadToRelative(113f, 47f)
            close()
        }
    }.build()
}
