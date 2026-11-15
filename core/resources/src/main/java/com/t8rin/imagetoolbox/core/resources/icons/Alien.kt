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

val Icons.Outlined.Alien: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Alien",
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
            moveTo(12f, 3f)
            curveToRelative(5f, 0f, 9f, 3.6f, 9f, 8f)
            reflectiveCurveToRelative(-5.5f, 10f, -9f, 10f)
            reflectiveCurveTo(3f, 15.4f, 3f, 11f)
            reflectiveCurveToRelative(4f, -8f, 9f, -8f)
            close()
            moveTo(6.5f, 10f)
            curveToRelative(1.6f, -0.7f, 4.5f, 2.5f, 3f, 4f)
            reflectiveCurveToRelative(-4.5f, -1.5f, -3f, -4f)
            close()
            moveTo(17.5f, 10f)
            curveToRelative(-1.6f, -0.7f, -4.5f, 2.5f, -3f, 4f)
            reflectiveCurveToRelative(4.5f, -1.5f, 3f, -4f)
            close()
            moveTo(10f, 17.5f)
            quadToRelative(2f, 1.5f, 4f, 0f)
        }
    }.build()
}