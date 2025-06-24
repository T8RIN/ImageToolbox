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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.ImageLimit: ImageVector by lazy {
    ImageVector.Builder(
        name = "Image Limit",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(7.6702f, 15.289f)
            lineToRelative(8.6596f, 0.0f)
            lineToRelative(-2.7061f, -3.6082f)
            lineToRelative(-2.1649f, 2.8865f)
            lineToRelative(-1.6237f, -2.1649f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.0f, 3.1136f)
            curveToRelative(-5.5228f, 0.0f, -10.0f, 4.4772f, -10.0f, 10.0f)
            curveToRelative(0.0f, 3.1172f, 1.4398f, 5.8854f, 3.6758f, 7.7189f)
            lineToRelative(2.6348f, -2.6348f)
            lineToRelative(-1.4087f, -1.4088f)
            lineToRelative(-1.2151f, 1.2151f)
            curveToRelative(-0.8453f, -1.0906f, -1.418f, -2.4037f, -1.6085f, -3.8388f)
            horizontalLineToRelative(1.7119f)
            verticalLineToRelative(-2.0f)
            horizontalLineTo(4.0599f)
            curveToRelative(0.1718f, -1.465f, 0.7408f, -2.8074f, 1.5935f, -3.9208f)
            lineToRelative(1.2126f, 1.2126f)
            lineToRelative(1.4142f, -1.4142f)
            lineTo(7.0601f, 6.8227f)
            curveTo(8.1728f, 5.9452f, 9.5237f, 5.3609f, 11.0f, 5.1748f)
            verticalLineToRelative(1.7205f)
            horizontalLineToRelative(2.0f)
            verticalLineTo(5.1747f)
            curveToRelative(1.4588f, 0.1837f, 2.7955f, 0.7556f, 3.9005f, 1.6158f)
            lineToRelative(-1.2166f, 1.2166f)
            lineToRelative(1.4142f, 1.4142f)
            lineToRelative(1.2186f, -1.2186f)
            curveToRelative(0.8701f, 1.1213f, 1.4495f, 2.4799f, 1.6234f, 3.9627f)
            horizontalLineToRelative(-1.7303f)
            verticalLineToRelative(2.0f)
            horizontalLineToRelative(1.7119f)
            curveToRelative(-0.1929f, 1.4529f, -0.776f, 2.7819f, -1.6387f, 3.8804f)
            lineToRelative(-1.2208f, -1.2208f)
            lineToRelative(-1.4142f, 1.4142f)
            lineToRelative(1.2112f, 1.2112f)
            lineToRelative(1.4319f, 1.4319f)
            horizontalLineToRelative(1.0E-4f)
            lineToRelative(0.0042f, 0.0042f)
            lineToRelative(0.0287f, -0.0287f)
            curveTo(20.567f, 19.0238f, 22.0f, 16.2367f, 22.0f, 13.1136f)
            curveTo(22.0f, 7.5908f, 17.5228f, 3.1136f, 12.0f, 3.1136f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ImageLimit: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ImageLimit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(7.67f, 15.289f)
            horizontalLineToRelative(8.66f)
            lineToRelative(-2.706f, -3.608f)
            lineToRelative(-2.165f, 2.886f)
            lineToRelative(-1.624f, -2.165f)
            lineToRelative(-2.165f, 2.887f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 3.114f)
            curveTo(6.477f, 3.114f, 2f, 7.591f, 2f, 13.114f)
            curveToRelative(0f, 3.117f, 1.44f, 5.885f, 3.676f, 7.719f)
            lineToRelative(2.635f, -2.635f)
            lineToRelative(-1.409f, -1.409f)
            lineToRelative(-1.215f, 1.215f)
            curveToRelative(-0.845f, -1.091f, -1.418f, -2.404f, -1.609f, -3.839f)
            horizontalLineToRelative(1.712f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-1.73f)
            curveToRelative(0.172f, -1.465f, 0.741f, -2.807f, 1.594f, -3.921f)
            lineToRelative(1.213f, 1.213f)
            lineToRelative(1.414f, -1.414f)
            lineToRelative(-1.22f, -1.22f)
            curveToRelative(1.113f, -0.878f, 2.464f, -1.462f, 3.94f, -1.648f)
            verticalLineToRelative(1.72f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-1.721f)
            curveToRelative(1.459f, 0.184f, 2.795f, 0.756f, 3.9f, 1.616f)
            lineToRelative(-1.217f, 1.217f)
            lineToRelative(1.414f, 1.414f)
            lineToRelative(1.219f, -1.219f)
            curveToRelative(0.87f, 1.121f, 1.449f, 2.48f, 1.623f, 3.963f)
            horizontalLineToRelative(-1.73f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(1.712f)
            curveToRelative(-0.193f, 1.453f, -0.776f, 2.782f, -1.639f, 3.88f)
            lineToRelative(-1.221f, -1.221f)
            lineToRelative(-1.414f, 1.414f)
            lineToRelative(1.211f, 1.211f)
            lineToRelative(1.432f, 1.432f)
            horizontalLineToRelative(0f)
            lineToRelative(0.004f, 0.004f)
            lineToRelative(0.029f, -0.029f)
            curveToRelative(2.243f, -1.834f, 3.676f, -4.621f, 3.676f, -7.744f)
            curveToRelative(0f, -5.523f, -4.477f, -10f, -10f, -10f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(20.554f, 18.274f)
            curveToRelative(0.912f, -1.508f, 1.446f, -3.27f, 1.446f, -5.16f)
            curveToRelative(0f, -5.523f, -4.477f, -10f, -10f, -10f)
            reflectiveCurveTo(2f, 7.591f, 2f, 13.114f)
            curveToRelative(0f, 1.841f, 0.506f, 3.559f, 1.373f, 5.04f)
            lineToRelative(17.18f, 0.12f)
            close()
        }
    }.build()
}