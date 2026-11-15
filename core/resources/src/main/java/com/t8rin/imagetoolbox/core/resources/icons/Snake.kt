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
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(9f, 5.5f)
            curveTo(9f, 4.1f, 10.6f, 3f, 12.5f, 3f)
            reflectiveCurveTo(16f, 4.1f, 16f, 5.5f)
            reflectiveCurveTo(14.4f, 8f, 12.5f, 8f)
            reflectiveCurveTo(9f, 6.9f, 9f, 5.5f)
            close()
            moveTo(9f, 5.5f)
            horizontalLineTo(6f)
            lineTo(4f, 3.5f)
            moveTo(6f, 5.5f)
            lineTo(4f, 7.5f)
            moveTo(15.5f, 7f)
            curveToRelative(1.5f, 1f, 1.5f, 2.1f, 1.5f, 3.5f)
            verticalLineTo(12f)
            curveToRelative(0f, 1.7f, -1.3f, 3f, -3f, 3f)
            horizontalLineTo(8f)
            curveToRelative(-1.1f, 0f, -2f, 0.7f, -2f, 1.5f)
            reflectiveCurveTo(6.9f, 18f, 8f, 18f)
            horizontalLineToRelative(10.5f)
            curveToRelative(1.4f, 0f, 2.5f, 0.7f, 2.5f, 1.5f)
            reflectiveCurveTo(19.9f, 21f, 18.5f, 21f)
            horizontalLineTo(5f)
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.75f, 4.5f)
            arcToRelative(0.75f, 0.75f, 0f, true, true, -1.5f, 0f)
            arcToRelative(0.75f, 0.75f, 0f, true, true, 1.5f, 0f)
            close()
        }
    }.build()
}