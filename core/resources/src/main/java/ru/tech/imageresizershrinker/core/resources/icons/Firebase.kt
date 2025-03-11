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

val Icons.TwoTone.Firebase: ImageVector by lazy {
    Builder(
        name = "Firebase", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFFFF9100)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(8.4f, 23.3f)
            curveToRelative(1.0f, 0.4f, 2.1f, 0.6f, 3.2f, 0.7f)
            curveToRelative(1.5f, 0.1f, 3.0f, -0.3f, 4.3f, -0.9f)
            curveToRelative(-1.6f, -0.6f, -3.0f, -1.5f, -4.2f, -2.7f)
            curveTo(11.0f, 21.7f, 9.8f, 22.7f, 8.4f, 23.3f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFFFC400)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.8f, 20.5f)
            curveTo(9.0f, 17.9f, 7.3f, 14.2f, 7.4f, 10.1f)
            curveToRelative(0.0f, -0.1f, 0.0f, -0.3f, 0.0f, -0.4f)
            curveTo(7.0f, 9.5f, 6.4f, 9.5f, 5.9f, 9.5f)
            curveToRelative(-0.8f, 0.0f, -1.5f, 0.1f, -2.2f, 0.3f)
            curveTo(3.0f, 11.0f, 2.5f, 12.5f, 2.5f, 14.1f)
            curveToRelative(-0.1f, 4.1f, 2.4f, 7.7f, 6.0f, 9.2f)
            curveTo(9.8f, 22.7f, 11.0f, 21.7f, 11.8f, 20.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFFF9100)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(11.8f, 20.5f)
            curveToRelative(0.6f, -1.0f, 1.0f, -2.3f, 1.1f, -3.6f)
            curveToRelative(0.1f, -3.4f, -2.2f, -6.4f, -5.4f, -7.2f)
            curveToRelative(0.0f, 0.1f, 0.0f, 0.3f, 0.0f, 0.4f)
            curveTo(7.3f, 14.2f, 9.0f, 17.9f, 11.8f, 20.5f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFDD2C00)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.5f, 0.0f)
            curveToRelative(-1.8f, 1.5f, -3.3f, 3.4f, -4.1f, 5.6f)
            curveTo(7.9f, 6.9f, 7.6f, 8.2f, 7.5f, 9.7f)
            curveToRelative(3.2f, 0.8f, 5.5f, 3.8f, 5.4f, 7.2f)
            curveToRelative(0.0f, 1.3f, -0.4f, 2.5f, -1.1f, 3.6f)
            curveToRelative(1.2f, 1.1f, 2.6f, 2.0f, 4.2f, 2.7f)
            curveToRelative(3.2f, -1.5f, 5.4f, -4.6f, 5.5f, -8.3f)
            curveToRelative(0.1f, -2.4f, -0.8f, -4.6f, -2.2f, -6.4f)
            curveTo(18.0f, 6.5f, 12.5f, 0.0f, 12.5f, 0.0f)
            close()
        }
    }.build()
}

val Icons.Outlined.Firebase: ImageVector by lazy {
    Builder(
        name = "Outlined.Firebase",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = SolidColor(Color(0xFF000000)),
            strokeLineWidth = 0.5f
        ) {
            moveTo(18.213f, 8.974f)
            curveToRelative(-0.449f, -0.623f, -1.482f, -1.904f, -3.068f, -3.808f)
            curveToRelative(-0.689f, -0.826f, -1.279f, -1.526f, -1.57f, -1.87f)
            curveToRelative(-0.16f, -0.19f, -0.298f, -0.352f, -0.406f, -0.481f)
            lineToRelative(-0.173f, -0.204f)
            lineToRelative(-0.094f, -0.111f)
            lineToRelative(-0.018f, -0.027f)
            lineToRelative(-0.008f, -0.004f)
            lineTo(12.475f, 2f)
            lineToRelative(-0.507f, 0.407f)
            curveToRelative(-1.295f, 1.038f, -2.356f, 2.375f, -3.067f, 3.866f)
            curveTo(8.464f, 7.16f, 8.18f, 8.027f, 8.03f, 8.92f)
            curveTo(7.991f, 9.121f, 7.958f, 9.328f, 7.93f, 9.535f)
            curveTo(7.756f, 9.52f, 7.579f, 9.511f, 7.403f, 9.507f)
            curveToRelative(-0.015f, -0.001f, -0.029f, -0.002f, -0.049f, -0.002f)
            curveToRelative(-0.643f, -0.022f, -1.282f, 0.054f, -1.9f, 0.228f)
            lineTo(5.19f, 9.808f)
            lineToRelative(-0.136f, 0.238f)
            curveToRelative(-0.637f, 1.118f, -0.998f, 2.39f, -1.043f, 3.68f)
            curveToRelative(-0.058f, 1.674f, 0.398f, 3.295f, 1.319f, 4.687f)
            curveToRelative(0.902f, 1.361f, 2.175f, 2.402f, 3.683f, 3.01f)
            lineToRelative(0.196f, 0.079f)
            lineToRelative(0.059f, 0.021f)
            lineToRelative(0.003f, -0.001f)
            curveToRelative(0.786f, 0.285f, 1.61f, 0.445f, 2.451f, 0.473f)
            curveTo(11.818f, 21.998f, 11.913f, 22f, 12.008f, 22f)
            curveToRelative(1.061f, 0f, 2.094f, -0.208f, 3.074f, -0.618f)
            lineToRelative(0.007f, 0.003f)
            lineToRelative(0.261f, -0.121f)
            curveToRelative(1.322f, -0.611f, 2.454f, -1.573f, 3.272f, -2.78f)
            curveToRelative(0.842f, -1.242f, 1.315f, -2.694f, 1.367f, -4.201f)
            curveToRelative(0.063f, -1.801f, -0.535f, -3.587f, -1.777f, -5.309f)
            lineTo(18.213f, 8.974f)
            close()
            moveTo(12.31f, 14.554f)
            curveToRelative(0.274f, 1.037f, 0.22f, 2.033f, -0.159f, 2.965f)
            curveToRelative(-0.946f, -0.934f, -1.64f, -1.96f, -2.063f, -3.054f)
            curveToRelative(-0.453f, -1.17f, -0.726f, -2.283f, -0.812f, -3.313f)
            curveToRelative(0.4f, 0.131f, 0.768f, 0.305f, 1.096f, 0.519f)
            curveToRelative(0.943f, 0.614f, 1.595f, 1.585f, 1.937f, 2.884f)
            verticalLineTo(14.554f)
            close()
            moveTo(12.483f, 19.572f)
            curveToRelative(0.402f, 0.306f, 0.825f, 0.593f, 1.26f, 0.857f)
            curveToRelative(-0.642f, 0.175f, -1.304f, 0.251f, -1.974f, 0.227f)
            curveToRelative(-0.103f, -0.004f, -0.207f, -0.01f, -0.311f, -0.018f)
            curveToRelative(0.382f, -0.329f, 0.725f, -0.686f, 1.024f, -1.066f)
            horizontalLineTo(12.483f)
            close()
            moveTo(13.605f, 14.212f)
            curveToRelative(-0.43f, -1.631f, -1.272f, -2.864f, -2.502f, -3.665f)
            curveToRelative(-0.539f, -0.351f, -1.154f, -0.618f, -1.829f, -0.792f)
            curveTo(9.284f, 9.644f, 9.296f, 9.532f, 9.311f, 9.423f)
            curveToRelative(0.012f, -0.094f, 0.025f, -0.18f, 0.039f, -0.261f)
            curveToRelative(0.111f, -0.574f, 0.277f, -1.142f, 0.491f, -1.687f)
            curveToRelative(0.082f, -0.209f, 0.172f, -0.417f, 0.267f, -0.617f)
            lineToRelative(0.004f, -0.007f)
            curveToRelative(0.147f, -0.298f, 0.313f, -0.599f, 0.508f, -0.92f)
            lineToRelative(0.077f, -0.126f)
            lineToRelative(-0.003f, -0.002f)
            curveToRelative(0.454f, -0.709f, 0.997f, -1.355f, 1.619f, -1.925f)
            lineToRelative(0.24f, 0.284f)
            curveToRelative(0.56f, 0.663f, 1.086f, 1.29f, 1.564f, 1.864f)
            curveToRelative(1.076f, 1.291f, 2.472f, 2.986f, 3.01f, 3.734f)
            curveToRelative(1.065f, 1.476f, 1.578f, 2.982f, 1.525f, 4.479f)
            curveToRelative(-0.041f, 1.162f, -0.385f, 2.296f, -0.996f, 3.277f)
            curveToRelative(-0.578f, 0.93f, -1.384f, 1.708f, -2.334f, 2.256f)
            curveToRelative(-0.53f, -0.264f, -1.299f, -0.699f, -2.116f, -1.332f)
            curveToRelative(0.659f, -1.313f, 0.793f, -2.734f, 0.399f, -4.227f)
            horizontalLineTo(13.605f)
            close()
            moveTo(11.459f, 18.71f)
            curveToRelative(-0.604f, 0.782f, -1.322f, 1.291f, -1.741f, 1.547f)
            curveToRelative(-0.068f, -0.025f, -0.136f, -0.051f, -0.203f, -0.078f)
            lineTo(9.461f, 20.158f)
            curveToRelative(-1.242f, -0.513f, -2.289f, -1.38f, -3.029f, -2.509f)
            curveToRelative(-0.756f, -1.153f, -1.131f, -2.494f, -1.082f, -3.877f)
            curveTo(5.384f, 12.78f, 5.631f, 11.833f, 6.085f, 10.955f)
            curveToRelative(0.263f, -0.058f, 0.532f, -0.095f, 0.8f, -0.109f)
            lineToRelative(0.069f, -0.002f)
            curveToRelative(0.136f, -0.003f, 0.27f, -0.003f, 0.399f, 0f)
            curveToRelative(0.189f, 0.009f, 0.378f, 0.028f, 0.564f, 0.058f)
            curveToRelative(0.061f, 1.26f, 0.37f, 2.62f, 0.921f, 4.043f)
            curveToRelative(0.53f, 1.37f, 1.411f, 2.635f, 2.62f, 3.763f)
            lineTo(11.459f, 18.71f)
            close()
        }
    }.build()
}