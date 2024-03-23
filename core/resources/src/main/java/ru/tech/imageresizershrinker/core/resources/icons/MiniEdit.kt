/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.MiniEdit: ImageVector by lazy {
    ImageVector.Builder(
        name = "Mini Edit", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(18.7912f, 7.9369f)
            curveToRelative(-0.1161f, -0.2838f, -0.2774f, -0.5418f, -0.4837f, -0.7739f)
            lineToRelative(-1.4317f, -1.4318f)
            curveToRelative(-0.2322f, -0.2321f, -0.4902f, -0.4062f, -0.7739f, -0.5223f)
            curveToRelative(-0.2838f, -0.1161f, -0.5804f, -0.1741f, -0.89f, -0.1741f)
            curveToRelative(-0.2838f, 0.0f, -0.5676f, 0.0516f, -0.8513f, 0.1547f)
            curveToRelative(-0.2838f, 0.1032f, -0.5418f, 0.2709f, -0.7739f, 0.5031f)
            lineToRelative(-8.5519f, 8.5132f)
            verticalLineToRelative(4.7596f)
            horizontalLineToRelative(4.7596f)
            lineToRelative(8.5132f, -8.5132f)
            curveToRelative(0.2321f, -0.2322f, 0.3998f, -0.4966f, 0.5031f, -0.7933f)
            curveToRelative(0.1032f, -0.2966f, 0.1547f, -0.5869f, 0.1547f, -0.8707f)
            curveTo(18.9653f, 8.5044f, 18.9073f, 8.2206f, 18.7912f, 7.9369f)
            close()
            moveTo(8.8269f, 16.6435f)
            horizontalLineTo(7.3565f)
            verticalLineToRelative(-1.4705f)
            lineToRelative(4.7209f, -4.6823f)
            lineToRelative(0.7353f, 0.6965f)
            lineToRelative(0.6965f, 0.7352f)
            lineTo(8.8269f, 16.6435f)
            close()
        }
    }.build()
}