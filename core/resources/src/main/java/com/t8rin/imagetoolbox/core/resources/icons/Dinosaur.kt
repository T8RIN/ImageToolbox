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

val Icons.Outlined.Dinosaur: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Dinosaur",
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
            moveTo(3f, 12f)
            arcToRelative(9f, 9f, 0f, true, false, 18f, 0f)
            arcToRelative(9f, 9f, 0f, true, false, -18f, 0f)
            moveTo(13.47f, 20.882f)
            lineToRelative(-1.47f, -5.882f)
            curveToRelative(-2.649f, -0.088f, -5f, -1.624f, -5f, -3.5f)
            curveToRelative(0f, -1.933f, 2.239f, -3.5f, 5f, -3.5f)
            reflectiveCurveToRelative(4f, 1f, 5f, 3f)
            curveToRelative(0.024f, 0.048f, 0.69f, 2.215f, 2f, 6.5f)
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.75f, 11f)
            arcToRelative(0.75f, 0.75f, 0f, true, true, -1.5f, 0f)
            arcToRelative(0.75f, 0.75f, 0f, true, true, 1.5f, 0f)
            close()
        }
    }.build()
}