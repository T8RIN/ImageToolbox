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

val Icons.Rounded.Star: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Star",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 691f)
            lineTo(314f, 791f)
            quadToRelative(-11f, 7f, -23f, 6f)
            reflectiveQuadToRelative(-21f, -8f)
            quadToRelative(-9f, -7f, -14f, -17.5f)
            reflectiveQuadToRelative(-2f, -23.5f)
            lineToRelative(44f, -189f)
            lineToRelative(-147f, -127f)
            quadToRelative(-10f, -9f, -12.5f, -20.5f)
            reflectiveQuadTo(140f, 389f)
            quadToRelative(4f, -11f, 12f, -18f)
            reflectiveQuadToRelative(22f, -9f)
            lineToRelative(194f, -17f)
            lineToRelative(75f, -178f)
            quadToRelative(5f, -12f, 15.5f, -18f)
            reflectiveQuadToRelative(21.5f, -6f)
            quadToRelative(11f, 0f, 21.5f, 6f)
            reflectiveQuadToRelative(15.5f, 18f)
            lineToRelative(75f, 178f)
            lineToRelative(194f, 17f)
            quadToRelative(14f, 2f, 22f, 9f)
            reflectiveQuadToRelative(12f, 18f)
            quadToRelative(4f, 11f, 1.5f, 22.5f)
            reflectiveQuadTo(809f, 432f)
            lineTo(662f, 559f)
            lineToRelative(44f, 189f)
            quadToRelative(3f, 13f, -2f, 23.5f)
            reflectiveQuadTo(690f, 789f)
            quadToRelative(-9f, 7f, -21f, 8f)
            reflectiveQuadToRelative(-23f, -6f)
            lineTo(480f, 691f)
            close()
        }
    }.build()
}

val Icons.Outlined.Star: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Star",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(354f, 673f)
            lineToRelative(126f, -76f)
            lineToRelative(126f, 77f)
            lineToRelative(-33f, -144f)
            lineToRelative(111f, -96f)
            lineToRelative(-146f, -13f)
            lineToRelative(-58f, -136f)
            lineToRelative(-58f, 135f)
            lineToRelative(-146f, 13f)
            lineToRelative(111f, 97f)
            lineToRelative(-33f, 143f)
            close()
            moveTo(480f, 691f)
            lineTo(314f, 791f)
            quadToRelative(-11f, 7f, -23f, 6f)
            reflectiveQuadToRelative(-21f, -8f)
            quadToRelative(-9f, -7f, -14f, -17.5f)
            reflectiveQuadToRelative(-2f, -23.5f)
            lineToRelative(44f, -189f)
            lineToRelative(-147f, -127f)
            quadToRelative(-10f, -9f, -12.5f, -20.5f)
            reflectiveQuadTo(140f, 389f)
            quadToRelative(4f, -11f, 12f, -18f)
            reflectiveQuadToRelative(22f, -9f)
            lineToRelative(194f, -17f)
            lineToRelative(75f, -178f)
            quadToRelative(5f, -12f, 15.5f, -18f)
            reflectiveQuadToRelative(21.5f, -6f)
            quadToRelative(11f, 0f, 21.5f, 6f)
            reflectiveQuadToRelative(15.5f, 18f)
            lineToRelative(75f, 178f)
            lineToRelative(194f, 17f)
            quadToRelative(14f, 2f, 22f, 9f)
            reflectiveQuadToRelative(12f, 18f)
            quadToRelative(4f, 11f, 1.5f, 22.5f)
            reflectiveQuadTo(809f, 432f)
            lineTo(662f, 559f)
            lineToRelative(44f, 189f)
            quadToRelative(3f, 13f, -2f, 23.5f)
            reflectiveQuadTo(690f, 789f)
            quadToRelative(-9f, 7f, -21f, 8f)
            reflectiveQuadToRelative(-23f, -6f)
            lineTo(480f, 691f)
            close()
            moveTo(480f, 490f)
            close()
        }
    }.build()
}
