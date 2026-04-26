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

val Icons.Outlined.CheckCircle: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.CheckCircle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(424f, 552f)
            lineToRelative(-86f, -86f)
            quadToRelative(-11f, -11f, -28f, -11f)
            reflectiveQuadToRelative(-28f, 11f)
            quadToRelative(-11f, 11f, -11f, 28f)
            reflectiveQuadToRelative(11f, 28f)
            lineToRelative(114f, 114f)
            quadToRelative(12f, 12f, 28f, 12f)
            reflectiveQuadToRelative(28f, -12f)
            lineToRelative(226f, -226f)
            quadToRelative(11f, -11f, 11f, -28f)
            reflectiveQuadToRelative(-11f, -28f)
            quadToRelative(-11f, -11f, -28f, -11f)
            reflectiveQuadToRelative(-28f, 11f)
            lineTo(424f, 552f)
            close()
            moveTo(480f, 880f)
            quadToRelative(-83f, 0f, -156f, -31.5f)
            reflectiveQuadTo(197f, 763f)
            quadToRelative(-54f, -54f, -85.5f, -127f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -83f, 31.5f, -156f)
            reflectiveQuadTo(197f, 197f)
            quadToRelative(54f, -54f, 127f, -85.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(83f, 0f, 156f, 31.5f)
            reflectiveQuadTo(763f, 197f)
            quadToRelative(54f, 54f, 85.5f, 127f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 83f, -31.5f, 156f)
            reflectiveQuadTo(763f, 763f)
            quadToRelative(-54f, 54f, -127f, 85.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(480f, 800f)
            quadToRelative(134f, 0f, 227f, -93f)
            reflectiveQuadToRelative(93f, -227f)
            quadToRelative(0f, -134f, -93f, -227f)
            reflectiveQuadToRelative(-227f, -93f)
            quadToRelative(-134f, 0f, -227f, 93f)
            reflectiveQuadToRelative(-93f, 227f)
            quadToRelative(0f, 134f, 93f, 227f)
            reflectiveQuadToRelative(227f, 93f)
            close()
            moveTo(480f, 480f)
            close()
        }
    }.build()
}

val Icons.Rounded.CheckCircle: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.CheckCircle",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(424f, 552f)
            lineToRelative(-86f, -86f)
            quadToRelative(-11f, -11f, -28f, -11f)
            reflectiveQuadToRelative(-28f, 11f)
            quadToRelative(-11f, 11f, -11f, 28f)
            reflectiveQuadToRelative(11f, 28f)
            lineToRelative(114f, 114f)
            quadToRelative(12f, 12f, 28f, 12f)
            reflectiveQuadToRelative(28f, -12f)
            lineToRelative(226f, -226f)
            quadToRelative(11f, -11f, 11f, -28f)
            reflectiveQuadToRelative(-11f, -28f)
            quadToRelative(-11f, -11f, -28f, -11f)
            reflectiveQuadToRelative(-28f, 11f)
            lineTo(424f, 552f)
            close()
            moveTo(480f, 880f)
            quadToRelative(-83f, 0f, -156f, -31.5f)
            reflectiveQuadTo(197f, 763f)
            quadToRelative(-54f, -54f, -85.5f, -127f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -83f, 31.5f, -156f)
            reflectiveQuadTo(197f, 197f)
            quadToRelative(54f, -54f, 127f, -85.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(83f, 0f, 156f, 31.5f)
            reflectiveQuadTo(763f, 197f)
            quadToRelative(54f, 54f, 85.5f, 127f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 83f, -31.5f, 156f)
            reflectiveQuadTo(763f, 763f)
            quadToRelative(-54f, 54f, -127f, 85.5f)
            reflectiveQuadTo(480f, 880f)
            close()
        }
    }.build()
}