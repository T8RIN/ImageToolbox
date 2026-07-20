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

val Icons.Outlined.WbAuto: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.WbAuto",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(267f, 528f)
            horizontalLineToRelative(112f)
            lineToRelative(21f, 57f)
            quadToRelative(2f, 7f, 8f, 11f)
            reflectiveQuadToRelative(13f, 4f)
            quadToRelative(12f, 0f, 18.5f, -9.5f)
            reflectiveQuadTo(442f, 570f)
            lineToRelative(-87f, -234f)
            quadToRelative(-3f, -8f, -9f, -12f)
            reflectiveQuadToRelative(-14f, -4f)
            horizontalLineToRelative(-18f)
            quadToRelative(-8f, 0f, -14f, 4f)
            reflectiveQuadToRelative(-9f, 12f)
            lineToRelative(-87f, 234f)
            quadToRelative(-4f, 11f, 2.5f, 20.5f)
            reflectiveQuadTo(225f, 600f)
            quadToRelative(8f, 0f, 13.5f, -4f)
            reflectiveQuadToRelative(7.5f, -11f)
            lineToRelative(21f, -57f)
            close()
            moveTo(281f, 488f)
            lineTo(321f, 372f)
            horizontalLineToRelative(4f)
            lineToRelative(40f, 116f)
            horizontalLineToRelative(-84f)
            close()
            moveTo(323f, 760f)
            quadToRelative(-117f, 0f, -198.5f, -81.5f)
            reflectiveQuadTo(43f, 480f)
            quadToRelative(0f, -117f, 81.5f, -198.5f)
            reflectiveQuadTo(323f, 200f)
            quadToRelative(78f, 0f, 144f, 40f)
            reflectiveQuadToRelative(102f, 109f)
            horizontalLineToRelative(-2f)
            quadToRelative(-3f, -11f, 4f, -20f)
            reflectiveQuadToRelative(18f, -9f)
            quadToRelative(8f, 0f, 14f, 4.5f)
            reflectiveQuadToRelative(8f, 12.5f)
            lineToRelative(44f, 187f)
            horizontalLineToRelative(2f)
            lineToRelative(54f, -189f)
            quadToRelative(2f, -7f, 7.5f, -11f)
            reflectiveQuadToRelative(12.5f, -4f)
            horizontalLineToRelative(12f)
            quadToRelative(7f, 0f, 12.5f, 4f)
            reflectiveQuadToRelative(7.5f, 11f)
            lineToRelative(54f, 189f)
            horizontalLineToRelative(4f)
            lineToRelative(44f, -187f)
            quadToRelative(2f, -8f, 8f, -12.5f)
            reflectiveQuadToRelative(14f, -4.5f)
            quadToRelative(11f, 0f, 18f, 9f)
            reflectiveQuadToRelative(4f, 20f)
            lineToRelative(-62f, 235f)
            quadToRelative(-2f, 8f, -7.5f, 12f)
            reflectiveQuadToRelative(-13.5f, 4f)
            horizontalLineToRelative(-12f)
            quadToRelative(-8f, 0f, -14f, -4.5f)
            reflectiveQuadToRelative(-8f, -11.5f)
            lineToRelative(-55f, -194f)
            horizontalLineToRelative(-2f)
            lineToRelative(-53f, 194f)
            quadToRelative(-2f, 8f, -8f, 12f)
            reflectiveQuadToRelative(-14f, 4f)
            horizontalLineToRelative(-11f)
            quadToRelative(-8f, 0f, -14f, -4.5f)
            reflectiveQuadToRelative(-8f, -12.5f)
            lineToRelative(-24f, -99f)
            quadToRelative(0f, 115f, -82f, 195.5f)
            reflectiveQuadTo(323f, 760f)
            close()
            moveTo(323f, 680f)
            quadToRelative(83f, 0f, 141.5f, -58.5f)
            reflectiveQuadTo(523f, 480f)
            quadToRelative(0f, -83f, -58.5f, -141.5f)
            reflectiveQuadTo(323f, 280f)
            quadToRelative(-83f, 0f, -141.5f, 58.5f)
            reflectiveQuadTo(123f, 480f)
            quadToRelative(0f, 83f, 58.5f, 141.5f)
            reflectiveQuadTo(323f, 680f)
            close()
            moveTo(323f, 480f)
            close()
        }
    }.build()
}
