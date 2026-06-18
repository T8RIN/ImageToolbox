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

val Icons.Rounded.Interests: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Interests",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(113f, 381f)
            lineToRelative(132f, -238f)
            quadToRelative(6f, -11f, 15f, -16f)
            reflectiveQuadToRelative(20f, -5f)
            quadToRelative(11f, 0f, 20f, 5f)
            reflectiveQuadToRelative(15f, 16f)
            lineToRelative(132f, 238f)
            quadToRelative(5f, 10f, 4.5f, 20f)
            reflectiveQuadToRelative(-5.5f, 19f)
            quadToRelative(-5f, 9f, -14f, 14.5f)
            reflectiveQuadToRelative(-20f, 5.5f)
            lineTo(148f, 440f)
            quadToRelative(-11f, 0f, -20f, -5.5f)
            reflectiveQuadTo(114f, 420f)
            quadToRelative(-5f, -9f, -5.5f, -19f)
            reflectiveQuadToRelative(4.5f, -20f)
            close()
            moveTo(167f, 793f)
            quadToRelative(-47f, -47f, -47f, -113f)
            reflectiveQuadToRelative(47f, -113f)
            quadToRelative(47f, -47f, 113f, -47f)
            reflectiveQuadToRelative(113f, 47f)
            quadToRelative(47f, 47f, 47f, 113f)
            reflectiveQuadToRelative(-47f, 113f)
            quadToRelative(-47f, 47f, -113f, 47f)
            reflectiveQuadToRelative(-113f, -47f)
            close()
            moveTo(520f, 800f)
            verticalLineToRelative(-240f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(560f, 520f)
            horizontalLineToRelative(240f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 560f)
            verticalLineToRelative(240f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 840f)
            lineTo(560f, 840f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(520f, 800f)
            close()
            moveTo(654f, 418f)
            lineTo(601f, 373f)
            quadToRelative(-69f, -58f, -95f, -91.5f)
            reflectiveQuadTo(480f, 207f)
            quadToRelative(0f, -45f, 31.5f, -76f)
            reflectiveQuadToRelative(78.5f, -31f)
            quadToRelative(27f, 0f, 50.5f, 12.5f)
            reflectiveQuadTo(680f, 147f)
            quadToRelative(16f, -22f, 39.5f, -34.5f)
            reflectiveQuadTo(770f, 100f)
            quadToRelative(47f, 0f, 78.5f, 31f)
            reflectiveQuadToRelative(31.5f, 76f)
            quadToRelative(0f, 41f, -26f, 74.5f)
            reflectiveQuadTo(759f, 373f)
            lineToRelative(-53f, 45f)
            quadToRelative(-11f, 10f, -26f, 10f)
            reflectiveQuadToRelative(-26f, -10f)
            close()
        }
    }.build()
}
