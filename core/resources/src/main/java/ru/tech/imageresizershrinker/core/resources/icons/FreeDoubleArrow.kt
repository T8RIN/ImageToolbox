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

val Icons.Rounded.FreeDoubleArrow: ImageVector by lazy {
    Builder(
        name = "FreeDoubleArrow", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(21.7125f, 6.2875f)
            curveTo(21.5208f, 6.0958f, 21.2833f, 6.0f, 21.0f, 6.0f)
            horizontalLineToRelative(-4.0f)
            curveToRelative(-0.2833f, 0.0f, -0.5208f, 0.0958f, -0.7125f, 0.2875f)
            curveTo(16.0958f, 6.4791f, 16.0f, 6.7167f, 16.0f, 7.0f)
            reflectiveCurveToRelative(0.0958f, 0.5208f, 0.2875f, 0.7125f)
            curveTo(16.4792f, 7.9042f, 16.7167f, 8.0f, 17.0f, 8.0f)
            horizontalLineToRelative(1.575f)
            lineTo(14.125f, 12.45f)
            curveTo(13.925f, 12.65f, 13.6875f, 12.75f, 13.4125f, 12.75f)
            curveToRelative(-0.275f, 0.0f, -0.5125f, -0.1f, -0.7125f, -0.3f)
            lineToRelative(-1.15f, -1.15f)
            curveToRelative(-0.5833f, -0.5833f, -1.2916f, -0.875f, -2.125f, -0.875f)
            curveToRelative(-0.8333f, 0.0f, -1.5416f, 0.2917f, -2.125f, 0.875f)
            lineTo(4.2344f, 14.3656f)
            lineToRelative(-0.2177f, 0.2165f)
            verticalLineToRelative(-1.5488f)
            curveToRelative(0.0f, -0.2786f, -0.0942f, -0.5122f, -0.2827f, -0.7007f)
            curveToRelative(-0.1885f, -0.1884f, -0.4221f, -0.2827f, -0.7007f, -0.2827f)
            curveToRelative(-0.2786f, 0.0f, -0.5121f, 0.0942f, -0.7006f, 0.2827f)
            curveToRelative(-0.1885f, 0.1885f, -0.2827f, 0.4221f, -0.2827f, 0.7007f)
            verticalLineToRelative(3.9333f)
            curveToRelative(0.0f, 0.2786f, 0.0942f, 0.5121f, 0.2827f, 0.7006f)
            curveToRelative(0.1885f, 0.1885f, 0.4221f, 0.2828f, 0.7006f, 0.2828f)
            horizontalLineToRelative(3.9333f)
            curveToRelative(0.2786f, 0.0f, 0.5121f, -0.0942f, 0.7006f, -0.2828f)
            curveToRelative(0.1885f, -0.1885f, 0.2827f, -0.422f, 0.2827f, -0.7006f)
            curveToRelative(0.0f, -0.2786f, -0.0942f, -0.5122f, -0.2827f, -0.7007f)
            curveToRelative(-0.1885f, -0.1884f, -0.422f, -0.2827f, -0.7006f, -0.2827f)
            horizontalLineTo(5.4179f)
            lineToRelative(1.4227f, -1.4227f)
            curveTo(6.8403f, 14.5605f, 6.8402f, 14.5602f, 6.84f, 14.5601f)
            lineToRelative(1.86f, -1.86f)
            curveToRelative(0.1833f, -0.1833f, 0.4166f, -0.275f, 0.7f, -0.275f)
            curveToRelative(0.2833f, 0.0f, 0.5167f, 0.0917f, 0.7f, 0.275f)
            lineTo(11.275f, 13.875f)
            curveToRelative(0.5833f, 0.5833f, 1.2916f, 0.875f, 2.125f, 0.875f)
            curveToRelative(0.8333f, 0.0f, 1.5416f, -0.2917f, 2.125f, -0.875f)
            lineTo(20.0f, 9.425f)
            verticalLineTo(11.0f)
            curveToRelative(0.0f, 0.2833f, 0.0958f, 0.5208f, 0.2875f, 0.7125f)
            curveTo(20.4792f, 11.9042f, 20.7167f, 12.0f, 21.0f, 12.0f)
            reflectiveCurveToRelative(0.5208f, -0.0958f, 0.7125f, -0.2875f)
            curveTo(21.9042f, 11.5208f, 22.0f, 11.2833f, 22.0f, 11.0f)
            verticalLineTo(7.0f)
            curveTo(22.0f, 6.7167f, 21.9042f, 6.4791f, 21.7125f, 6.2875f)
            close()
        }
    }
        .build()
}