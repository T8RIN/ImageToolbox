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

val Icons.Outlined.MotionMode: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.MotionMode",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 194f)
            verticalLineToRelative(572f)
            quadToRelative(-17f, -17f, -32f, -36f)
            reflectiveQuadToRelative(-28f, -39f)
            verticalLineToRelative(-422f)
            quadToRelative(13f, -20f, 28f, -39f)
            reflectiveQuadToRelative(32f, -36f)
            close()
            moveTo(360f, 98f)
            verticalLineToRelative(764f)
            quadToRelative(-21f, -7f, -41f, -15.5f)
            reflectiveQuadTo(280f, 827f)
            verticalLineToRelative(-694f)
            quadToRelative(19f, -11f, 39f, -19.5f)
            reflectiveQuadToRelative(41f, -15.5f)
            close()
            moveTo(640f, 847f)
            verticalLineToRelative(-734f)
            quadToRelative(106f, 47f, 173f, 145f)
            reflectiveQuadToRelative(67f, 222f)
            quadToRelative(0f, 124f, -67f, 222f)
            reflectiveQuadTo(640f, 847f)
            close()
            moveTo(480f, 880f)
            quadToRelative(-10f, 0f, -20f, -0.5f)
            reflectiveQuadTo(440f, 878f)
            verticalLineToRelative(-796f)
            quadToRelative(10f, -1f, 20f, -1.5f)
            reflectiveQuadToRelative(20f, -0.5f)
            quadToRelative(20f, 0f, 40f, 2f)
            reflectiveQuadToRelative(40f, 6f)
            verticalLineToRelative(784f)
            quadToRelative(-20f, 4f, -40f, 6f)
            reflectiveQuadToRelative(-40f, 2f)
            close()
        }
    }.build()
}