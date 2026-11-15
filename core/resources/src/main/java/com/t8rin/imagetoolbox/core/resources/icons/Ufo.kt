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

val Icons.Outlined.Ufo: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Ufo",
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
            moveTo(7f, 11f)
            arcToRelative(5f, 5f, 0f, false, true, 10f, 0f)
            moveTo(7f, 11f)
            curveToRelative(-3f, 0.6f, -5f, 1.7f, -5f, 3f)
            reflectiveCurveToRelative(4.5f, 4f, 10f, 4f)
            reflectiveCurveToRelative(10f, -2.7f, 10f, -4f)
            reflectiveCurveToRelative(-2f, -2.4f, -5f, -3f)
            moveTo(7f, 11f)
            curveToRelative(2.7f, 1.3f, 7.3f, 1.3f, 10f, 0f)
            moveTo(6f, 15f)
            horizontalLineToRelative(0.01f)
            moveTo(12f, 16f)
            horizontalLineToRelative(0.01f)
            moveTo(18f, 15f)
            horizontalLineToRelative(0.01f)
            moveTo(9f, 21f)
            lineToRelative(1f, -3f)
            moveToRelative(4f, 0f)
            lineToRelative(1f, 3f)
        }
    }.build()
}