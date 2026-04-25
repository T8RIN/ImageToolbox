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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Telegram: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Telegram",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(18.687f, 5.371f)
            curveToRelative(0.123f, -0.002f, 0.394f, 0.028f, 0.571f, 0.172f)
            curveToRelative(0.117f, 0.102f, 0.192f, 0.244f, 0.21f, 0.399f)
            curveToRelative(0.02f, 0.114f, 0.044f, 0.376f, 0.025f, 0.579f)
            curveToRelative(-0.221f, 2.33f, -1.181f, 7.983f, -1.67f, 10.592f)
            curveToRelative(-0.206f, 1.105f, -0.613f, 1.475f, -1.007f, 1.51f)
            curveToRelative(-0.855f, 0.08f, -1.504f, -0.565f, -2.333f, -1.107f)
            curveToRelative(-1.296f, -0.851f, -2.029f, -1.38f, -3.288f, -2.21f)
            curveToRelative(-1.455f, -0.958f, -0.512f, -1.486f, 0.317f, -2.345f)
            curveToRelative(0.217f, -0.226f, 3.986f, -3.655f, 4.06f, -3.966f)
            curveToRelative(0.009f, -0.039f, 0.017f, -0.184f, -0.069f, -0.26f)
            reflectiveCurveToRelative(-0.214f, -0.05f, -0.306f, -0.029f)
            curveToRelative(-0.13f, 0.029f, -2.201f, 1.4f, -6.214f, 4.107f)
            curveToRelative(-0.589f, 0.405f, -1.121f, 0.602f, -1.599f, 0.589f)
            curveToRelative(-0.525f, -0.01f, -1.537f, -0.296f, -2.29f, -0.54f)
            curveToRelative(-0.923f, -0.301f, -1.656f, -0.459f, -1.592f, -0.969f)
            curveToRelative(0.033f, -0.265f, 0.399f, -0.537f, 1.096f, -0.814f)
            curveToRelative(4.295f, -1.871f, 7.158f, -3.105f, 8.592f, -3.7f)
            curveToRelative(4.091f, -1.702f, 4.942f, -1.998f, 5.495f, -2.007f)
            close()
        }
    }.build()
}