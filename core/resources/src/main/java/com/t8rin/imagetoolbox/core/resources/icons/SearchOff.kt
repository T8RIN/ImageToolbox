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

val Icons.Outlined.SearchOff: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SearchOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(280f, 708f)
            lineToRelative(56f, 57f)
            quadToRelative(6f, 6f, 14f, 6f)
            reflectiveQuadToRelative(14f, -6f)
            quadToRelative(6f, -6f, 6f, -14.5f)
            reflectiveQuadToRelative(-6f, -14.5f)
            lineToRelative(-56f, -56f)
            lineToRelative(57f, -57f)
            quadToRelative(6f, -6f, 6f, -14f)
            reflectiveQuadToRelative(-6f, -14f)
            quadToRelative(-6f, -6f, -14f, -6f)
            reflectiveQuadToRelative(-14f, 6f)
            lineToRelative(-57f, 57f)
            lineToRelative(-57f, -57f)
            quadToRelative(-6f, -6f, -14f, -6f)
            reflectiveQuadToRelative(-14f, 6f)
            quadToRelative(-6f, 6f, -6f, 14f)
            reflectiveQuadToRelative(6f, 14f)
            lineToRelative(57f, 57f)
            lineToRelative(-57f, 57f)
            quadToRelative(-6f, 6f, -6f, 14f)
            reflectiveQuadToRelative(6f, 14f)
            quadToRelative(6f, 6f, 14f, 6f)
            reflectiveQuadToRelative(14f, -6f)
            lineToRelative(57f, -57f)
            close()
            moveTo(138.5f, 821.5f)
            quadTo(80f, 763f, 80f, 680f)
            reflectiveQuadToRelative(58.5f, -141.5f)
            quadTo(197f, 480f, 280f, 480f)
            reflectiveQuadToRelative(141.5f, 58.5f)
            quadTo(480f, 597f, 480f, 680f)
            reflectiveQuadToRelative(-58.5f, 141.5f)
            quadTo(363f, 880f, 280f, 880f)
            reflectiveQuadToRelative(-141.5f, -58.5f)
            close()
            moveTo(568f, 584f)
            quadToRelative(-12f, -13f, -25.5f, -26.5f)
            reflectiveQuadTo(516f, 532f)
            quadToRelative(38f, -24f, 61f, -64f)
            reflectiveQuadToRelative(23f, -88f)
            quadToRelative(0f, -75f, -52.5f, -127.5f)
            reflectiveQuadTo(420f, 200f)
            quadToRelative(-75f, 0f, -127.5f, 52.5f)
            reflectiveQuadTo(240f, 380f)
            quadToRelative(0f, 6f, 0.5f, 11.5f)
            reflectiveQuadTo(242f, 403f)
            quadToRelative(-18f, 2f, -39.5f, 8f)
            reflectiveQuadTo(164f, 425f)
            quadToRelative(-2f, -11f, -3f, -22f)
            reflectiveQuadToRelative(-1f, -23f)
            quadToRelative(0f, -109f, 75.5f, -184.5f)
            reflectiveQuadTo(420f, 120f)
            quadToRelative(109f, 0f, 184.5f, 75.5f)
            reflectiveQuadTo(680f, 380f)
            quadToRelative(0f, 43f, -13.5f, 81.5f)
            reflectiveQuadTo(629f, 532f)
            lineToRelative(223f, 224f)
            quadToRelative(11f, 11f, 11.5f, 27.5f)
            reflectiveQuadTo(852f, 812f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineTo(568f, 584f)
            close()
        }
    }.build()
}
