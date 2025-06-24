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
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.ImageEdit: ImageVector by lazy {
    Builder(
        name = "Image Edit", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.0182f, 18.6079f)
            horizontalLineTo(5.393f)
            verticalLineTo(5.4684f)
            horizontalLineToRelative(13.214f)
            verticalLineToRelative(3.5806f)
            verticalLineToRelative(1.5046f)
            lineToRelative(0.035f, -0.0348f)
            curveToRelative(0.178f, -0.178f, 0.3758f, -0.3066f, 0.5934f, -0.3857f)
            curveToRelative(0.2175f, -0.0791f, 0.4351f, -0.1187f, 0.6527f, -0.1187f)
            curveToRelative(0.2104f, 0.0f, 0.4118f, 0.0408f, 0.6066f, 0.1107f)
            verticalLineTo(9.5792f)
            verticalLineTo(9.0491f)
            verticalLineTo(5.4684f)
            curveToRelative(0.0f, -1.0324f, -0.84f, -1.8771f, -1.8877f, -1.8771f)
            horizontalLineTo(5.393f)
            curveToRelative(-1.0382f, 0.0f, -1.8877f, 0.8447f, -1.8877f, 1.8771f)
            verticalLineToRelative(13.1395f)
            curveToRelative(0.0f, 1.0417f, 0.8495f, 1.8771f, 1.8877f, 1.8771f)
            horizontalLineToRelative(5.6252f)
            horizontalLineToRelative(0.0379f)
            horizontalLineToRelative(0.7311f)
            verticalLineToRelative(-1.8771f)
            horizontalLineToRelative(-0.6839f)
            horizontalLineTo(11.0182f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.4588f, 13.8637f)
            curveToRelative(-0.075f, -0.1833f, -0.1792f, -0.35f, -0.3125f, -0.5f)
            lineToRelative(-0.925f, -0.925f)
            curveToRelative(-0.15f, -0.15f, -0.3167f, -0.2625f, -0.5f, -0.3375f)
            curveToRelative(-0.1833f, -0.075f, -0.375f, -0.1125f, -0.575f, -0.1125f)
            curveToRelative(-0.1833f, 0.0f, -0.3667f, 0.0333f, -0.55f, 0.1f)
            curveToRelative(-0.1833f, 0.0667f, -0.35f, 0.175f, -0.5f, 0.325f)
            lineToRelative(-5.525f, 5.5f)
            verticalLineToRelative(3.075f)
            horizontalLineToRelative(3.075f)
            lineToRelative(5.5f, -5.5f)
            curveToRelative(0.15f, -0.15f, 0.2583f, -0.3209f, 0.325f, -0.5125f)
            curveToRelative(0.0667f, -0.1917f, 0.1f, -0.3792f, 0.1f, -0.5625f)
            reflectiveCurveTo(22.5338f, 14.0469f, 22.4588f, 13.8637f)
            close()
            moveTo(16.0213f, 19.4887f)
            horizontalLineToRelative(-0.95f)
            verticalLineToRelative(-0.95f)
            lineToRelative(3.05f, -3.025f)
            lineToRelative(0.475f, 0.45f)
            lineToRelative(0.45f, 0.475f)
            lineTo(16.0213f, 19.4887f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(15.4244f, 13.7219f)
            lineToRelative(-1.4644f, -1.9469f)
            lineToRelative(-2.75f, 3.54f)
            lineToRelative(-1.96f, -2.36f)
            lineToRelative(-2.75f, 3.53f)
            lineToRelative(6.1487f, 0.0f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ImageEdit: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    Builder(
        name = "TwoTone.ImageEdit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(18.607f, 10.554f)
            lineToRelative(0.035f, -0.035f)
            curveToRelative(0.178f, -0.178f, 0.376f, -0.307f, 0.593f, -0.386f)
            curveToRelative(0.218f, -0.079f, 0.435f, -0.119f, 0.653f, -0.119f)
            curveToRelative(0.21f, 0f, 0.412f, 0.041f, 0.607f, 0.111f)
            verticalLineToRelative(-4.657f)
            curveToRelative(0f, -1.032f, -0.84f, -1.877f, -1.888f, -1.877f)
            horizontalLineTo(5.393f)
            curveToRelative(-1.038f, 0f, -1.888f, 0.845f, -1.888f, 1.877f)
            verticalLineToRelative(13.139f)
            curveToRelative(0f, 1.042f, 0.85f, 1.877f, 1.888f, 1.877f)
            horizontalLineToRelative(6.394f)
            verticalLineToRelative(-1.877f)
            horizontalLineToRelative(-0.684f)
            reflectiveCurveToRelative(-0.085f, 0f, -0.085f, 0f)
            horizontalLineToRelative(-5.625f)
            verticalLineTo(5.468f)
            horizontalLineToRelative(13.214f)
            verticalLineToRelative(5.085f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(16.021f, 19.489f)
            lineToRelative(-0.95f, 0f)
            lineToRelative(0f, -0.95f)
            lineToRelative(3.05f, -3.025f)
            lineToRelative(0.475f, 0.45f)
            lineToRelative(0.45f, 0.475f)
            lineToRelative(-3.025f, 3.05f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(15.424f, 13.722f)
            lineToRelative(-1.464f, -1.947f)
            lineToRelative(-2.75f, 3.54f)
            lineToRelative(-1.96f, -2.36f)
            lineToRelative(-2.75f, 3.53f)
            lineToRelative(6.149f, 0f)
            lineToRelative(2.776f, -2.763f)
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(22.459f, 13.864f)
            curveToRelative(-0.075f, -0.183f, -0.179f, -0.35f, -0.313f, -0.5f)
            lineToRelative(-0.925f, -0.925f)
            curveToRelative(-0.15f, -0.15f, -0.317f, -0.262f, -0.5f, -0.337f)
            reflectiveCurveToRelative(-0.375f, -0.112f, -0.575f, -0.112f)
            curveToRelative(-0.183f, 0f, -0.367f, 0.033f, -0.55f, 0.1f)
            reflectiveCurveToRelative(-0.35f, 0.175f, -0.5f, 0.325f)
            lineToRelative(-5.525f, 5.5f)
            verticalLineToRelative(3.075f)
            horizontalLineToRelative(3.075f)
            lineToRelative(5.5f, -5.5f)
            curveToRelative(0.15f, -0.15f, 0.258f, -0.321f, 0.325f, -0.513f)
            curveToRelative(0.067f, -0.192f, 0.1f, -0.379f, 0.1f, -0.563f)
            reflectiveCurveToRelative(-0.037f, -0.367f, -0.112f, -0.55f)
            close()
            moveTo(16.021f, 19.489f)
            horizontalLineToRelative(-0.95f)
            verticalLineToRelative(-0.95f)
            lineToRelative(3.05f, -3.025f)
            lineToRelative(0.475f, 0.45f)
            lineToRelative(0.45f, 0.475f)
            lineToRelative(-3.025f, 3.05f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5.173f, 5.044f)
            lineToRelative(0f, 13.567f)
            lineToRelative(6.614f, 0f)
            lineToRelative(0f, -1.258f)
            lineToRelative(-0.002f, -0.002f)
            lineToRelative(1.236f, -1.236f)
            lineToRelative(0.013f, 0.013f)
            lineToRelative(5.878f, -5.878f)
            lineToRelative(0f, -5.204f)
            lineToRelative(-13.739f, 0f)
            close()
        }
    }.build()
}