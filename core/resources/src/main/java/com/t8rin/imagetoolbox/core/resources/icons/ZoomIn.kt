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

val Icons.Outlined.ZoomIn: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ZoomIn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(340f, 420f)
            horizontalLineToRelative(-40f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(260f, 380f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(300f, 340f)
            horizontalLineToRelative(40f)
            verticalLineToRelative(-40f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(380f, 260f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(420f, 300f)
            verticalLineToRelative(40f)
            horizontalLineToRelative(40f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(500f, 380f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(460f, 420f)
            horizontalLineToRelative(-40f)
            verticalLineToRelative(40f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(380f, 500f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(340f, 460f)
            verticalLineToRelative(-40f)
            close()
            moveTo(380f, 640f)
            quadToRelative(-109f, 0f, -184.5f, -75.5f)
            reflectiveQuadTo(120f, 380f)
            quadToRelative(0f, -109f, 75.5f, -184.5f)
            reflectiveQuadTo(380f, 120f)
            quadToRelative(109f, 0f, 184.5f, 75.5f)
            reflectiveQuadTo(640f, 380f)
            quadToRelative(0f, 44f, -14f, 83f)
            reflectiveQuadToRelative(-38f, 69f)
            lineToRelative(224f, 224f)
            quadToRelative(11f, 11f, 11f, 28f)
            reflectiveQuadToRelative(-11f, 28f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            lineTo(532f, 588f)
            quadToRelative(-30f, 24f, -69f, 38f)
            reflectiveQuadToRelative(-83f, 14f)
            close()
            moveTo(380f, 560f)
            quadToRelative(75f, 0f, 127.5f, -52.5f)
            reflectiveQuadTo(560f, 380f)
            quadToRelative(0f, -75f, -52.5f, -127.5f)
            reflectiveQuadTo(380f, 200f)
            quadToRelative(-75f, 0f, -127.5f, 52.5f)
            reflectiveQuadTo(200f, 380f)
            quadToRelative(0f, 75f, 52.5f, 127.5f)
            reflectiveQuadTo(380f, 560f)
            close()
        }
    }.build()
}
