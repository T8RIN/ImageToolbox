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

val Icons.Rounded.Twitch: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Twitch",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(11.571f, 4.714f)
            horizontalLineToRelative(1.715f)
            verticalLineToRelative(5.143f)
            horizontalLineTo(11.57f)
            close()
            moveToRelative(4.715f, 0f)
            horizontalLineTo(18f)
            verticalLineToRelative(5.143f)
            horizontalLineToRelative(-1.714f)
            close()
            moveTo(6f, 0f)
            lineTo(1.714f, 4.286f)
            verticalLineToRelative(15.428f)
            horizontalLineToRelative(5.143f)
            verticalLineTo(24f)
            lineToRelative(4.286f, -4.286f)
            horizontalLineToRelative(3.428f)
            lineTo(22.286f, 12f)
            verticalLineTo(0f)
            close()
            moveToRelative(14.571f, 11.143f)
            lineToRelative(-3.428f, 3.428f)
            horizontalLineToRelative(-3.429f)
            lineToRelative(-3f, 3f)
            verticalLineToRelative(-3f)
            horizontalLineTo(6.857f)
            verticalLineTo(1.714f)
            horizontalLineToRelative(13.714f)
            close()
        }
    }.build()
}
