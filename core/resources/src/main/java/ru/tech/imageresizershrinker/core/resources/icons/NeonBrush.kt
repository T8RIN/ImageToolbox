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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.NeonBrush: ImageVector by lazy {
    Builder(
        name = "NeonBrush",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(19.443f, 6.414f)
            lineToRelative(-1.858f, -1.858f)
            curveToRelative(-0.354f, -0.354f, -0.774f, -0.531f, -1.261f, -0.531f)
            curveToRelative(-0.487f, 0f, -0.907f, 0.177f, -1.261f, 0.531f)
            lineTo(4.927f, 14.694f)
            lineToRelative(-0.876f, 4.22f)
            curveToRelative(-0.071f, 0.301f, 0.009f, 0.566f, 0.239f, 0.796f)
            curveToRelative(0.23f, 0.23f, 0.495f, 0.31f, 0.796f, 0.239f)
            lineToRelative(4.22f, -0.876f)
            lineTo(19.443f, 8.936f)
            curveToRelative(0.354f, -0.354f, 0.531f, -0.774f, 0.531f, -1.261f)
            curveTo(19.974f, 7.188f, 19.797f, 6.768f, 19.443f, 6.414f)
            close()
            moveTo(8.801f, 16.579f)
            lineToRelative(-1.38f, -1.38f)
            lineToRelative(8.89f, -8.89f)
            lineToRelative(1.38f, 1.38f)
            lineTo(8.801f, 16.579f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = SolidColor(Color(0xFF000000)),
            strokeAlpha = 0.3f,
            strokeLineWidth = 6f
        ) {
            moveTo(19.443f, 6.415f)
            lineToRelative(-1.858f, -1.858f)
            curveToRelative(-0.354f, -0.354f, -0.774f, -0.531f, -1.261f, -0.531f)
            reflectiveCurveToRelative(-0.907f, 0.177f, -1.261f, 0.531f)
            lineTo(5.04f, 14.581f)
            curveToRelative(-0.074f, 0.074f, -0.124f, 0.168f, -0.146f, 0.27f)
            lineToRelative(-0.843f, 4.063f)
            curveToRelative(-0.071f, 0.301f, 0.009f, 0.566f, 0.239f, 0.796f)
            curveToRelative(0.23f, 0.23f, 0.495f, 0.31f, 0.796f, 0.239f)
            lineToRelative(4.063f, -0.843f)
            curveToRelative(0.102f, -0.021f, 0.196f, -0.072f, 0.27f, -0.146f)
            lineTo(19.443f, 8.936f)
            curveToRelative(0.354f, -0.354f, 0.531f, -0.774f, 0.531f, -1.261f)
            curveTo(19.974f, 7.188f, 19.797f, 6.768f, 19.443f, 6.415f)
            close()
            moveTo(18.698f, 9.026f)
            lineToRelative(-8.888f, 8.888f)
            curveTo(9.178f, 18.546f, 8.153f, 18.546f, 7.521f, 17.914f)
            horizontalLineTo(7.521f)
            curveToRelative(-0.154f, -0.006f, -0.308f, -0.009f, -0.459f, -0.047f)
            curveToRelative(-0.285f, -0.072f, -0.569f, -0.226f, -0.777f, -0.435f)
            curveToRelative(-0.29f, -0.293f, -0.397f, -0.639f, -0.444f, -1.036f)
            curveToRelative(-0.007f, -0.055f, -0.001f, -0.11f, -0.004f, -0.165f)
            lineToRelative(-0.055f, -0.055f)
            curveToRelative(-0.423f, -0.423f, -0.423f, -1.109f, 0f, -1.532f)
            lineToRelative(9.267f, -9.267f)
            curveToRelative(0.632f, -0.632f, 1.657f, -0.632f, 2.289f, 0f)
            lineToRelative(1.36f, 1.36f)
            curveTo(19.33f, 7.369f, 19.33f, 8.394f, 18.698f, 9.026f)
            close()
        }
    }.build()
}
