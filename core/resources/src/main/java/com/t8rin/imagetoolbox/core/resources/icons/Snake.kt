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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.Snake: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Snake",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(14f, 6f)
            horizontalLineTo(7f)
            arcToRelative(4f, 4f, 0f, false, false, 0f, 8f)
            horizontalLineToRelative(10f)
            arcToRelative(3f, 3f, 0f, false, true, 0f, 6f)
            horizontalLineTo(8f)
            quadToRelative(-4f, 0f, -5f, -2f)
            moveTo(17f, 3f)
            horizontalLineToRelative(2f)
            arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
            verticalLineToRelative(2f)
            arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
            horizontalLineToRelative(-2f)
            arcToRelative(3f, 3f, 0f, false, true, 0f, -6f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.25f, 5.75f)
            arcToRelative(0.75f, 0.75f, 0f, true, true, -1.5f, 0f)
            arcToRelative(0.75f, 0.75f, 0f, true, true, 1.5f, 0f)
            close()
        }
    }.build()
}