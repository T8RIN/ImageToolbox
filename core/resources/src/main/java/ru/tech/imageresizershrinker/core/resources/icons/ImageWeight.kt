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
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.ImageWeight: ImageVector by lazy {
    Builder(
        name = "Image Weight", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(6.0f, 19.0f)
            horizontalLineToRelative(12.0f)
            lineTo(16.575f, 9.0f)
            horizontalLineTo(7.425f)
            lineTo(6.0f, 19.0f)
            close()
            moveTo(12.0f, 7.0f)
            curveToRelative(0.2833f, 0.0f, 0.5208f, -0.0958f, 0.7125f, -0.2875f)
            reflectiveCurveTo(13.0f, 6.2833f, 13.0f, 6.0f)
            reflectiveCurveToRelative(-0.0958f, -0.5208f, -0.2875f, -0.7125f)
            curveTo(12.5208f, 5.0958f, 12.2833f, 5.0f, 12.0f, 5.0f)
            reflectiveCurveToRelative(-0.5208f, 0.0958f, -0.7125f, 0.2875f)
            curveTo(11.0958f, 5.4792f, 11.0f, 5.7167f, 11.0f, 6.0f)
            reflectiveCurveToRelative(0.0958f, 0.5208f, 0.2875f, 0.7125f)
            reflectiveCurveTo(11.7167f, 7.0f, 12.0f, 7.0f)
            close()
            moveTo(14.825f, 7.0f)
            horizontalLineToRelative(1.75f)
            curveToRelative(0.5f, 0.0f, 0.9333f, 0.1667f, 1.3f, 0.5f)
            curveToRelative(0.3667f, 0.3333f, 0.5917f, 0.7417f, 0.675f, 1.225f)
            lineToRelative(1.425f, 10.0f)
            curveToRelative(0.0833f, 0.6f, -0.0708f, 1.1292f, -0.4625f, 1.5875f)
            curveTo(19.1208f, 20.7708f, 18.6167f, 21.0f, 18.0f, 21.0f)
            horizontalLineTo(6.0f)
            curveToRelative(-0.6167f, 0.0f, -1.1208f, -0.2292f, -1.5125f, -0.6875f)
            reflectiveCurveToRelative(-0.5458f, -0.9875f, -0.4625f, -1.5875f)
            lineToRelative(1.425f, -10.0f)
            curveTo(5.5333f, 8.2417f, 5.7583f, 7.8333f, 6.125f, 7.5f)
            curveToRelative(0.3667f, -0.3333f, 0.8f, -0.5f, 1.3f, -0.5f)
            horizontalLineToRelative(1.75f)
            curveTo(9.125f, 6.8333f, 9.0833f, 6.6708f, 9.05f, 6.5125f)
            reflectiveCurveTo(9.0f, 6.1833f, 9.0f, 6.0f)
            curveToRelative(0.0f, -0.8333f, 0.2917f, -1.5417f, 0.875f, -2.125f)
            curveToRelative(0.5833f, -0.5833f, 1.2917f, -0.875f, 2.125f, -0.875f)
            reflectiveCurveToRelative(1.5417f, 0.2917f, 2.125f, 0.875f)
            reflectiveCurveTo(15.0f, 5.1667f, 15.0f, 6.0f)
            curveToRelative(0.0f, 0.1833f, -0.0167f, 0.3542f, -0.05f, 0.5125f)
            reflectiveCurveTo(14.875f, 6.8333f, 14.825f, 7.0f)
            close()
            moveTo(6.0f, 19.0f)
            horizontalLineToRelative(12.0f)
            horizontalLineTo(6.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(15.2442f, 15.1263f)
            curveToRelative(-0.1654f, -0.2197f, -0.4357f, -0.5795f, -0.6007f, -0.7995f)
            lineToRelative(-0.9704f, -1.2938f)
            curveToRelative(-0.165f, -0.22f, -0.435f, -0.22f, -0.6f, 0.0f)
            lineToRelative(-1.275f, 1.7f)
            curveToRelative(-0.165f, 0.22f, -0.4354f, 0.2203f, -0.6009f, 7.0E-4f)
            lineToRelative(-0.7733f, -1.0263f)
            curveToRelative(-0.1655f, -0.2196f, -0.4361f, -0.2195f, -0.6014f, 2.0E-4f)
            lineToRelative(-1.5239f, 2.0259f)
            curveToRelative(-0.1653f, 0.2198f, -0.0756f, 0.3996f, 0.1994f, 0.3996f)
            horizontalLineToRelative(0.9267f)
            curveToRelative(0.275f, 0.0f, 0.725f, 0.0f, 1.0f, 0.0f)
            horizontalLineToRelative(3.1464f)
            curveToRelative(0.275f, 0.0f, 0.725f, 0.0f, 1.0f, 0.0f)
            horizontalLineToRelative(0.9308f)
            curveToRelative(0.275f, 0.0f, 0.3647f, -0.1798f, 0.1993f, -0.3995f)
            lineTo(15.2442f, 15.1263f)
            close()
        }
    }.build()
}