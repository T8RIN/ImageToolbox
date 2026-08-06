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

val Icons.Rounded.Threads: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Threads",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(18.263f, 11.097f)
            curveToRelative(-0.03f, -3.486f, -1.92f, -5.586f, -5.111f, -5.586f)
            curveToRelative(-2.13f, 0f, -3.922f, 0.963f, -4.863f, 2.499f)
            lineToRelative(2.062f, 1.438f)
            curveToRelative(0.535f, -0.843f, 1.272f, -1.543f, 2.628f, -1.543f)
            curveToRelative(1.528f, 0f, 2.318f, 0.85f, 2.544f, 2.431f)
            arcToRelative(
                15f,
                15f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                -2.236f,
                -0.173f
            )
            curveToRelative(-4.125f, 0f, -6.068f, 1.867f, -6.068f, 4.336f)
            reflectiveCurveToRelative(1.943f, 3.99f, 4.804f, 3.99f)
            curveToRelative(3.139f, 0f, 5.013f, -2.115f, 5.781f, -4.735f)
            curveToRelative(0.798f, 0.361f, 1.348f, 1.204f, 1.348f, 2.47f)
            curveToRelative(0f, 3.387f, -3.907f, 5.232f, -7.22f, 5.232f)
            curveToRelative(-4.885f, 0f, -8.077f, -3.207f, -8.077f, -8.424f)
            curveToRelative(0f, -6.392f, 4.223f, -10.487f, 9.9f, -10.487f)
            curveToRelative(3.808f, 0f, 5.69f, 1.671f, 6.97f, 3.914f)
            lineToRelative(2.108f, -1.475f)
            curveTo(21.44f, 2.078f, 18.331f, 0f, 13.663f, 0f)
            curveTo(6.227f, 0f, 1.168f, 5.277f, 1.168f, 12.934f)
            curveToRelative(0f, 7f, 4.953f, 11.066f, 10.856f, 11.066f)
            curveToRelative(4.878f, 0f, 9.809f, -2.846f, 9.809f, -7.716f)
            curveToRelative(0f, -2.545f, -1.46f, -4.231f, -3.569f, -5.187f)
            moveToRelative(-6.33f, 4.855f)
            curveToRelative(-1.077f, 0f, -2.026f, -0.512f, -2.026f, -1.453f)
            curveToRelative(0f, -1.483f, 1.822f, -1.934f, 3.606f, -1.934f)
            curveToRelative(0.678f, 0f, 1.34f, 0.045f, 1.927f, 0.173f)
            curveToRelative(-0.422f, 1.927f, -1.671f, 3.215f, -3.508f, 3.214f)
            close()
        }
    }.build()
}
