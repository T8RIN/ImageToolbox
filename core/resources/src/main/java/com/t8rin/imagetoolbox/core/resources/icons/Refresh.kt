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

val Icons.Rounded.Refresh: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Refresh",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 800f)
            quadToRelative(-134f, 0f, -227f, -93f)
            reflectiveQuadToRelative(-93f, -227f)
            quadToRelative(0f, -134f, 93f, -227f)
            reflectiveQuadToRelative(227f, -93f)
            quadToRelative(69f, 0f, 132f, 28.5f)
            reflectiveQuadTo(720f, 270f)
            verticalLineToRelative(-70f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(760f, 160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(800f, 200f)
            verticalLineToRelative(200f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(760f, 440f)
            lineTo(560f, 440f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 400f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(560f, 360f)
            horizontalLineToRelative(128f)
            quadToRelative(-32f, -56f, -87.5f, -88f)
            reflectiveQuadTo(480f, 240f)
            quadToRelative(-100f, 0f, -170f, 70f)
            reflectiveQuadToRelative(-70f, 170f)
            quadToRelative(0f, 100f, 70f, 170f)
            reflectiveQuadToRelative(170f, 70f)
            quadToRelative(68f, 0f, 124.5f, -34.5f)
            reflectiveQuadTo(692f, 593f)
            quadToRelative(8f, -14f, 22.5f, -19.5f)
            reflectiveQuadToRelative(29.5f, -0.5f)
            quadToRelative(16f, 5f, 23f, 21f)
            reflectiveQuadToRelative(-1f, 30f)
            quadToRelative(-41f, 80f, -117f, 128f)
            reflectiveQuadToRelative(-169f, 48f)
            close()
        }
    }.build()
}
