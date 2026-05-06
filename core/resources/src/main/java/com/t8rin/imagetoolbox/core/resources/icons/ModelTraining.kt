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

val Icons.Rounded.ModelTraining: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.ModelTraining",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(440f, 740f)
            quadToRelative(0f, -23f, -15.5f, -45.5f)
            reflectiveQuadToRelative(-34.5f, -47f)
            quadToRelative(-19f, -24.5f, -34.5f, -51f)
            reflectiveQuadTo(340f, 540f)
            quadToRelative(0f, -58f, 41f, -99f)
            reflectiveQuadToRelative(99f, -41f)
            quadToRelative(58f, 0f, 99f, 41f)
            reflectiveQuadToRelative(41f, 99f)
            quadToRelative(0f, 30f, -15.5f, 56.5f)
            reflectiveQuadToRelative(-34.5f, 51f)
            quadToRelative(-19f, 24.5f, -34.5f, 47f)
            reflectiveQuadTo(520f, 740f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(451.5f, 828.5f)
            quadTo(440f, 817f, 440f, 800f)
            verticalLineToRelative(-20f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(20f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(480f, 840f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            close()
            moveTo(720f, 720f)
            quadToRelative(-9f, -9f, -10.5f, -23f)
            reflectiveQuadToRelative(6.5f, -25f)
            quadToRelative(21f, -33f, 32.5f, -71.5f)
            reflectiveQuadTo(760f, 520f)
            quadToRelative(0f, -55f, -19.5f, -103.5f)
            reflectiveQuadTo(686f, 330f)
            quadToRelative(-11f, -13f, -11.5f, -28.5f)
            reflectiveQuadTo(686f, 274f)
            quadToRelative(12f, -12f, 29f, -12f)
            reflectiveQuadToRelative(28f, 12f)
            quadToRelative(45f, 48f, 71f, 111f)
            reflectiveQuadToRelative(26f, 135f)
            quadToRelative(0f, 54f, -15f, 103.5f)
            reflectiveQuadTo(783f, 715f)
            quadToRelative(-11f, 17f, -30f, 18f)
            reflectiveQuadToRelative(-33f, -13f)
            close()
            moveTo(177f, 715f)
            quadToRelative(-27f, -42f, -42f, -91.5f)
            reflectiveQuadTo(120f, 520f)
            quadToRelative(0f, -150f, 105f, -255f)
            reflectiveQuadToRelative(255f, -105f)
            horizontalLineToRelative(8f)
            lineToRelative(-36f, -36f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            lineToRelative(104f, 104f)
            quadToRelative(6f, 6f, 8.5f, 13f)
            reflectiveQuadToRelative(2.5f, 15f)
            quadToRelative(0f, 8f, -2.5f, 15f)
            reflectiveQuadToRelative(-8.5f, 13f)
            lineTo(508f, 332f)
            quadToRelative(-12f, 12f, -28f, 11.5f)
            reflectiveQuadTo(452f, 331f)
            quadToRelative(-12f, -12f, -12f, -28.5f)
            reflectiveQuadToRelative(12f, -28.5f)
            lineToRelative(34f, -34f)
            horizontalLineToRelative(-6f)
            quadToRelative(-116f, 0f, -198f, 82f)
            reflectiveQuadToRelative(-82f, 198f)
            quadToRelative(0f, 42f, 11.5f, 80.5f)
            reflectiveQuadTo(244f, 672f)
            quadToRelative(8f, 11f, 6.5f, 25f)
            reflectiveQuadTo(240f, 720f)
            quadToRelative(-14f, 14f, -33f, 13f)
            reflectiveQuadToRelative(-30f, -18f)
            close()
        }
    }.build()
}
