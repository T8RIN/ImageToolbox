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

val Icons.Outlined.TextFields: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.TextFields",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(340f, 800f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(280f, 740f)
            verticalLineToRelative(-460f)
            lineTo(140f, 280f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(80f, 220f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(140f, 160f)
            horizontalLineToRelative(400f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(600f, 220f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(540f, 280f)
            lineTo(400f, 280f)
            verticalLineToRelative(460f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(340f, 800f)
            close()
            moveTo(700f, 800f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(640f, 740f)
            verticalLineToRelative(-260f)
            horizontalLineToRelative(-60f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(520f, 420f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(580f, 360f)
            horizontalLineToRelative(240f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(880f, 420f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(820f, 480f)
            horizontalLineToRelative(-60f)
            verticalLineToRelative(260f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(700f, 800f)
            close()
        }
    }.build()
}

val Icons.Rounded.TextFields: ImageVector get() = Icons.Outlined.TextFields