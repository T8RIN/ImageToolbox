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

val Icons.Rounded.Raw: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Raw",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(120f, 570f)
            verticalLineToRelative(-180f)
            quadToRelative(0f, -13f, 8.5f, -21.5f)
            reflectiveQuadTo(150f, 360f)
            horizontalLineToRelative(110f)
            quadToRelative(24f, 0f, 42f, 18f)
            reflectiveQuadToRelative(18f, 42f)
            verticalLineToRelative(40f)
            quadToRelative(0f, 18f, -9.5f, 32.5f)
            reflectiveQuadTo(284f, 516f)
            lineToRelative(20f, 46f)
            quadToRelative(6f, 14f, -2f, 26f)
            reflectiveQuadToRelative(-24f, 12f)
            quadToRelative(-8f, 0f, -15f, -4f)
            reflectiveQuadToRelative(-10f, -12f)
            lineToRelative(-29f, -64f)
            horizontalLineToRelative(-44f)
            verticalLineToRelative(50f)
            quadToRelative(0f, 13f, -8.5f, 21.5f)
            reflectiveQuadTo(150f, 600f)
            quadToRelative(-13f, 0f, -21.5f, -8.5f)
            reflectiveQuadTo(120f, 570f)
            close()
            moveTo(359f, 564f)
            lineTo(401f, 397f)
            quadToRelative(4f, -17f, 17f, -27f)
            reflectiveQuadToRelative(30f, -10f)
            horizontalLineToRelative(24f)
            quadToRelative(17f, 0f, 30f, 10f)
            reflectiveQuadToRelative(17f, 27f)
            lineToRelative(42f, 167f)
            quadToRelative(4f, 14f, -5f, 25f)
            reflectiveQuadToRelative(-23f, 11f)
            quadToRelative(-11f, 0f, -18.5f, -6.5f)
            reflectiveQuadTo(505f, 577f)
            lineToRelative(-9f, -37f)
            horizontalLineToRelative(-70f)
            lineToRelative(-10f, 38f)
            quadToRelative(-2f, 10f, -10f, 16f)
            reflectiveQuadToRelative(-19f, 6f)
            quadToRelative(-14f, 0f, -23f, -11f)
            reflectiveQuadToRelative(-5f, -25f)
            close()
            moveTo(614f, 578f)
            lineTo(569f, 396f)
            quadToRelative(-4f, -14f, 5f, -25f)
            reflectiveQuadToRelative(23f, -11f)
            quadToRelative(10f, 0f, 18.5f, 6f)
            reflectiveQuadToRelative(10.5f, 16f)
            lineToRelative(24f, 98f)
            lineToRelative(25f, -98f)
            quadToRelative(2f, -10f, 10f, -16f)
            reflectiveQuadToRelative(18f, -6f)
            horizontalLineToRelative(14f)
            quadToRelative(10f, 0f, 18.5f, 6f)
            reflectiveQuadToRelative(10.5f, 16f)
            lineToRelative(24f, 98f)
            lineToRelative(25f, -98f)
            quadToRelative(2f, -10f, 10f, -16f)
            reflectiveQuadToRelative(18f, -6f)
            quadToRelative(14f, 0f, 23f, 11f)
            reflectiveQuadToRelative(5f, 25f)
            lineToRelative(-46f, 182f)
            quadToRelative(-2f, 10f, -10f, 16f)
            reflectiveQuadToRelative(-18f, 6f)
            horizontalLineToRelative(-14f)
            quadToRelative(-10f, 0f, -18f, -6f)
            reflectiveQuadToRelative(-10f, -16f)
            lineToRelative(-25f, -100f)
            lineToRelative(-25f, 100f)
            quadToRelative(-2f, 10f, -10f, 16f)
            reflectiveQuadToRelative(-18f, 6f)
            horizontalLineToRelative(-14f)
            quadToRelative(-10f, 0f, -18.5f, -6f)
            reflectiveQuadTo(614f, 578f)
            close()
            moveTo(440f, 480f)
            horizontalLineToRelative(40f)
            lineToRelative(-10f, -40f)
            horizontalLineToRelative(-20f)
            lineToRelative(-10f, 40f)
            close()
            moveTo(180f, 460f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-40f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(40f)
            close()
        }
    }.build()
}
