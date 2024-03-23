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

val Icons.Filled.USDT: ImageVector by lazy {
    Builder(
        name = "USDT", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(18.7538f, 10.5176f)
            curveToRelative(0.0f, 0.6251f, -2.2379f, 1.1483f, -5.2381f, 1.2812f)
            lineToRelative(0.0028f, 7.0E-4f)
            curveToRelative(-0.0848f, 0.0064f, -0.5233f, 0.0325f, -1.5012f, 0.0325f)
            curveToRelative(-0.7778f, 0.0f, -1.33f, -0.0233f, -1.5237f, -0.0325f)
            curveToRelative(-3.0059f, -0.1322f, -5.2495f, -0.6555f, -5.2495f, -1.2819f)
            reflectiveCurveToRelative(2.2436f, -1.149f, 5.2495f, -1.2834f)
            verticalLineToRelative(2.0442f)
            curveToRelative(0.1965f, 0.0142f, 0.7594f, 0.0474f, 1.5372f, 0.0474f)
            curveToRelative(0.9334f, 0.0f, 1.4008f, -0.0389f, 1.4849f, -0.0466f)
            lineTo(13.5157f, 9.2356f)
            curveToRelative(2.9994f, 0.1337f, 5.2381f, 0.657f, 5.2381f, 1.282f)
            close()
            moveTo(23.9438f, 11.0642f)
            lineTo(12.1248f, 22.389f)
            arcToRelative(
                0.1803f, 0.1803f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                dx1 = -0.2496f,
                dy1 = 0.0f
            )
            lineTo(0.0562f, 11.0635f)
            arcToRelative(
                0.1781f, 0.1781f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                dx1 = -0.0382f,
                dy1 = -0.2079f
            )
            lineToRelative(4.3762f, -9.1921f)
            arcToRelative(
                0.1767f, 0.1767f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                dx1 = 0.1626f,
                dy1 = -0.1026f
            )
            horizontalLineToRelative(14.8878f)
            arcToRelative(
                0.1768f, 0.1768f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                dx1 = 0.1612f,
                dy1 = 0.1032f
            )
            lineToRelative(4.3762f, 9.1922f)
            arcToRelative(
                0.1782f, 0.1782f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                dx1 = -0.0382f,
                dy1 = 0.2079f
            )
            close()
            moveTo(19.4658f, 10.6604f)
            curveToRelative(0.0f, -0.8068f, -2.5515f, -1.4799f, -5.9473f, -1.6369f)
            lineTo(13.5185f, 7.195f)
            horizontalLineToRelative(4.186f)
            lineTo(17.7045f, 4.4055f)
            lineTo(6.3076f, 4.4055f)
            lineTo(6.3076f, 7.195f)
            horizontalLineToRelative(4.1852f)
            verticalLineToRelative(1.8286f)
            curveToRelative(-3.4018f, 0.1562f, -5.9601f, 0.83f, -5.9601f, 1.6376f)
            curveToRelative(0.0f, 0.8075f, 2.5583f, 1.4806f, 5.9601f, 1.6376f)
            verticalLineToRelative(5.8618f)
            horizontalLineToRelative(3.025f)
            verticalLineToRelative(-5.8639f)
            curveToRelative(3.394f, -0.1563f, 5.948f, -0.8295f, 5.948f, -1.6363f)
            close()
        }
    }.build()
}
