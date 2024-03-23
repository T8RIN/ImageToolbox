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

val Icons.Filled.Resize: ImageVector by lazy {
    Builder(
        name = "Resize", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(9.0024f, 4.0f)
            horizontalLineTo(8.0f)
            verticalLineToRelative(1.0024f)
            curveTo(8.0f, 4.4488f, 8.4488f, 4.0f, 9.0024f, 4.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(18.9976f, 4.0f)
            curveTo(19.5512f, 4.0f, 20.0f, 4.4488f, 20.0f, 5.0024f)
            verticalLineTo(4.0f)
            horizontalLineTo(18.9976f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(18.9976f, 16.0f)
            horizontalLineTo(20.0f)
            verticalLineToRelative(-1.0024f)
            curveTo(20.0f, 15.5512f, 19.5512f, 16.0f, 18.9976f, 16.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(20.0f, 2.0f)
            horizontalLineTo(8.0f)
            curveTo(6.8954f, 2.0f, 6.0f, 2.8954f, 6.0f, 4.0f)
            verticalLineToRelative(8.0f)
            horizontalLineTo(4.0f)
            curveToRelative(-1.1046f, 0.0f, -2.0f, 0.8954f, -2.0f, 2.0f)
            verticalLineToRelative(6.0f)
            curveToRelative(0.0f, 1.1046f, 0.8954f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(6.0f)
            curveToRelative(1.1046f, 0.0f, 2.0f, -0.8954f, 2.0f, -2.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(8.0f)
            curveToRelative(1.1046f, 0.0f, 2.0f, -0.8954f, 2.0f, -2.0f)
            verticalLineTo(4.0f)
            curveTo(22.0f, 2.8954f, 21.1046f, 2.0f, 20.0f, 2.0f)
            close()
            moveTo(18.3137f, 13.5529f)
            curveToRelative(-0.2004f, 0.1885f, -0.442f, 0.2828f, -0.7248f, 0.2828f)
            curveToRelative(-0.2711f, -0.0118f, -0.5038f, -0.109f, -0.6982f, -0.2917f)
            curveToRelative(-0.1945f, -0.1827f, -0.2917f, -0.4154f, -0.2917f, -0.6982f)
            verticalLineTo(8.8152f)
            lineToRelative(-4.5139f, 4.5139f)
            verticalLineToRelative(-1.0E-4f)
            lineTo(10.0f, 15.4142f)
            verticalLineTo(16.0f)
            verticalLineToRelative(0.7035f)
            verticalLineToRelative(0.3228f)
            verticalLineTo(17.75f)
            verticalLineToRelative(1.2473f)
            curveTo(10.0f, 19.5511f, 9.5511f, 20.0f, 8.9974f, 20.0f)
            horizontalLineTo(5.0027f)
            curveTo(4.4489f, 20.0f, 4.0f, 19.5511f, 4.0f, 18.9973f)
            verticalLineTo(17.75f)
            verticalLineToRelative(-2.7474f)
            curveTo(4.0f, 14.4489f, 4.4489f, 14.0f, 5.0027f, 14.0f)
            horizontalLineToRelative(1.3095f)
            horizontalLineToRelative(0.741f)
            horizontalLineToRelative(0.9415f)
            horizontalLineTo(8.0f)
            horizontalLineToRelative(0.5858f)
            lineToRelative(2.1463f, -2.1463f)
            horizontalLineToRelative(-1.0E-4f)
            lineToRelative(4.4527f, -4.4527f)
            horizontalLineToRelative(-4.0305f)
            curveToRelative(-0.2828f, 0.0f, -0.5156f, -0.0972f, -0.6982f, -0.2917f)
            curveToRelative(-0.1827f, -0.1944f, -0.2799f, -0.4272f, -0.2917f, -0.6982f)
            curveToRelative(0.0f, -0.2828f, 0.0943f, -0.5244f, 0.2828f, -0.7248f)
            curveToRelative(0.1885f, -0.2003f, 0.4243f, -0.3005f, 0.7071f, -0.3005f)
            horizontalLineToRelative(6.47f)
            curveToRelative(0.1414f, 0.0f, 0.2681f, 0.0266f, 0.3801f, 0.0796f)
            curveToRelative(0.112f, 0.053f, 0.2151f, 0.1266f, 0.3094f, 0.2209f)
            curveToRelative(0.0942f, 0.0943f, 0.1679f, 0.1974f, 0.2209f, 0.3094f)
            curveToRelative(0.053f, 0.1119f, 0.0795f, 0.2386f, 0.0795f, 0.3801f)
            lineToRelative(1.0E-4f, 6.47f)
            curveTo(18.6143f, 13.1286f, 18.514f, 13.3643f, 18.3137f, 13.5529f)
            close()
        }
    }.build()
}

val Icons.Outlined.Resize: ImageVector by lazy {
    Builder(
        name = "Resize", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(20.0f, 2.0f)
            horizontalLineTo(8.0f)
            curveTo(6.8954f, 2.0f, 6.0f, 2.8954f, 6.0f, 4.0f)
            verticalLineToRelative(8.0f)
            horizontalLineTo(4.0f)
            curveToRelative(-1.1046f, 0.0f, -2.0f, 0.8954f, -2.0f, 2.0f)
            verticalLineToRelative(6.0f)
            curveToRelative(0.0f, 1.1046f, 0.8954f, 2.0f, 2.0f, 2.0f)
            horizontalLineToRelative(6.0f)
            curveToRelative(1.1046f, 0.0f, 2.0f, -0.8954f, 2.0f, -2.0f)
            verticalLineToRelative(-2.0f)
            horizontalLineToRelative(8.0f)
            curveToRelative(1.1046f, 0.0f, 2.0f, -0.8954f, 2.0f, -2.0f)
            verticalLineTo(4.0f)
            curveTo(22.0f, 2.8954f, 21.1046f, 2.0f, 20.0f, 2.0f)
            close()
            moveTo(10.0f, 15.4142f)
            verticalLineToRelative(1.6121f)
            verticalLineTo(17.75f)
            verticalLineToRelative(1.2473f)
            curveTo(10.0f, 19.5511f, 9.5511f, 20.0f, 8.9974f, 20.0f)
            horizontalLineTo(5.0027f)
            curveTo(4.4489f, 20.0f, 4.0f, 19.5511f, 4.0f, 18.9973f)
            verticalLineTo(17.75f)
            verticalLineToRelative(-2.7474f)
            curveTo(4.0f, 14.4489f, 4.4489f, 14.0f, 5.0027f, 14.0f)
            horizontalLineToRelative(2.0505f)
            horizontalLineToRelative(0.9415f)
            horizontalLineToRelative(0.5911f)
            horizontalLineToRelative(0.4116f)
            curveTo(9.5511f, 14.0f, 10.0f, 14.4489f, 10.0f, 15.0026f)
            verticalLineTo(15.4142f)
            close()
            moveTo(20.0f, 16.0f)
            horizontalLineToRelative(-8.0f)
            verticalLineToRelative(-0.3495f)
            verticalLineTo(14.0f)
            curveToRelative(0.0f, -0.1779f, -0.0307f, -0.3472f, -0.0743f, -0.5115f)
            lineToRelative(4.6733f, -4.6733f)
            verticalLineToRelative(4.0305f)
            curveToRelative(0.0f, 0.2828f, 0.0972f, 0.5156f, 0.2917f, 0.6982f)
            curveToRelative(0.1944f, 0.1827f, 0.4272f, 0.2799f, 0.6982f, 0.2917f)
            curveToRelative(0.2828f, 0.0f, 0.5244f, -0.0943f, 0.7248f, -0.2828f)
            curveToRelative(0.2003f, -0.1885f, 0.3005f, -0.4243f, 0.3005f, -0.7071f)
            lineToRelative(-1.0E-4f, -6.47f)
            curveToRelative(0.0f, -0.1414f, -0.0265f, -0.2681f, -0.0795f, -0.3801f)
            curveToRelative(-0.053f, -0.112f, -0.1267f, -0.2151f, -0.2209f, -0.3094f)
            curveTo(18.2194f, 5.592f, 18.1163f, 5.5184f, 18.0043f, 5.4653f)
            curveToRelative(-0.1119f, -0.053f, -0.2386f, -0.0796f, -0.3801f, -0.0796f)
            horizontalLineToRelative(-6.47f)
            curveToRelative(-0.2828f, 0.0f, -0.5186f, 0.1002f, -0.7071f, 0.3005f)
            curveToRelative(-0.1885f, 0.2004f, -0.2828f, 0.442f, -0.2828f, 0.7248f)
            curveToRelative(0.0118f, 0.2711f, 0.109f, 0.5038f, 0.2917f, 0.6982f)
            curveToRelative(0.1827f, 0.1945f, 0.4154f, 0.2917f, 0.6982f, 0.2917f)
            horizontalLineToRelative(4.0305f)
            lineToRelative(-4.6733f, 4.6733f)
            curveTo(10.3472f, 12.0307f, 10.1779f, 12.0f, 10.0f, 12.0f)
            horizontalLineTo(9.9947f)
            horizontalLineTo(8.2906f)
            horizontalLineTo(8.0f)
            verticalLineTo(4.0f)
            horizontalLineToRelative(12.0f)
            verticalLineTo(16.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(9.0024f, 4.0f)
            horizontalLineTo(8.0f)
            verticalLineToRelative(1.0024f)
            curveTo(8.0f, 4.4488f, 8.4488f, 4.0f, 9.0024f, 4.0f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(18.9976f, 4.0f)
            curveTo(19.5512f, 4.0f, 20.0f, 4.4488f, 20.0f, 5.0024f)
            verticalLineTo(4.0f)
            horizontalLineTo(18.9976f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(18.9976f, 16.0f)
            horizontalLineTo(20.0f)
            verticalLineToRelative(-1.0024f)
            curveTo(20.0f, 15.5512f, 19.5512f, 16.0f, 18.9976f, 16.0f)
            close()
        }
    }.build()
}
