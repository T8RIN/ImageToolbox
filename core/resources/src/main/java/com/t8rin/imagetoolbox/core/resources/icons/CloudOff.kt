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

val Icons.Rounded.CloudOff: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.CloudOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(260f, 800f)
            quadToRelative(-92f, 0f, -156f, -64f)
            reflectiveQuadTo(40f, 580f)
            quadToRelative(0f, -77f, 47.5f, -137f)
            reflectiveQuadTo(210f, 366f)
            quadToRelative(3f, -8f, 6f, -15.5f)
            reflectiveQuadToRelative(6f, -16.5f)
            lineTo(84f, 196f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            quadToRelative(11f, -11f, 28f, -11f)
            reflectiveQuadToRelative(28f, 11f)
            lineToRelative(680f, 680f)
            quadToRelative(11f, 11f, 11.5f, 27.5f)
            reflectiveQuadTo(820f, 876f)
            quadToRelative(-11f, 11f, -27.5f, 11.5f)
            reflectiveQuadTo(764f, 877f)
            lineToRelative(-78f, -77f)
            lineTo(260f, 800f)
            close()
            moveTo(864f, 750f)
            lineTo(322f, 209f)
            quadToRelative(35f, -24f, 74.5f, -36.5f)
            reflectiveQuadTo(480f, 160f)
            quadToRelative(117f, 0f, 198.5f, 81.5f)
            reflectiveQuadTo(760f, 440f)
            quadToRelative(69f, 8f, 114.5f, 59.5f)
            reflectiveQuadTo(920f, 620f)
            quadToRelative(0f, 39f, -15f, 72.5f)
            reflectiveQuadTo(864f, 750f)
            close()
        }
    }.build()
}