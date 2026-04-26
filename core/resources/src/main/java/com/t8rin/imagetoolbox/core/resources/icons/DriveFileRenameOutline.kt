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

val Icons.Outlined.DriveFileRenameOutline: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.DriveFileRenameOutline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(10f, 21f)
            lineToRelative(4f, -4f)
            horizontalLineToRelative(6f)
            quadToRelative(0.825f, 0f, 1.413f, 0.587f)
            quadTo(22f, 18.175f, 22f, 19f)
            quadToRelative(0f, 0.825f, -0.587f, 1.413f)
            quadTo(20.825f, 21f, 20f, 21f)
            close()
            moveTo(4f, 19f)
            horizontalLineToRelative(1.4f)
            lineToRelative(8.625f, -8.625f)
            lineToRelative(-1.4f, -1.4f)
            lineTo(4f, 17.6f)
            close()
            moveTo(18.3f, 8.925f)
            lineToRelative(-4.25f, -4.2f)
            lineToRelative(1.4f, -1.4f)
            quadToRelative(0.575f, -0.575f, 1.413f, -0.575f)
            quadToRelative(0.837f, 0f, 1.412f, 0.575f)
            lineToRelative(1.4f, 1.4f)
            quadToRelative(0.575f, 0.575f, 0.6f, 1.388f)
            quadToRelative(0.025f, 0.812f, -0.55f, 1.387f)
            close()
            moveTo(3f, 21f)
            quadToRelative(-0.425f, 0f, -0.712f, -0.288f)
            quadTo(2f, 20.425f, 2f, 20f)
            verticalLineToRelative(-2.825f)
            quadToRelative(0f, -0.2f, 0.075f, -0.387f)
            quadToRelative(0.075f, -0.188f, 0.225f, -0.338f)
            lineToRelative(10.3f, -10.3f)
            lineToRelative(4.25f, 4.25f)
            lineToRelative(-10.3f, 10.3f)
            quadToRelative(-0.15f, 0.15f, -0.337f, 0.225f)
            quadToRelative(-0.188f, 0.075f, -0.388f, 0.075f)
            close()
            moveTo(13.325f, 9.675f)
            lineToRelative(-0.7f, -0.7f)
            lineToRelative(1.4f, 1.4f)
            close()
        }
    }.build()
}
