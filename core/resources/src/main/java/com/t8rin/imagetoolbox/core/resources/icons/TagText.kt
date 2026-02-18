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

val Icons.Outlined.TagText: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TagText",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.665f, 12.11f)
            lineTo(12.89f, 3.335f)
            curveToRelative(-0.39f, -0.39f, -0.877f, -0.585f, -1.365f, -0.585f)
            horizontalLineToRelative(-6.825f)
            curveToRelative(-1.072f, 0f, -1.95f, 0.878f, -1.95f, 1.95f)
            verticalLineToRelative(6.825f)
            curveToRelative(0f, 0.488f, 0.195f, 0.975f, 0.585f, 1.365f)
            lineToRelative(8.775f, 8.775f)
            curveToRelative(0.39f, 0.39f, 0.877f, 0.585f, 1.365f, 0.585f)
            reflectiveCurveToRelative(0.975f, -0.195f, 1.365f, -0.585f)
            lineToRelative(6.825f, -6.825f)
            curveToRelative(0.39f, -0.39f, 0.585f, -0.877f, 0.585f, -1.365f)
            reflectiveCurveToRelative(-0.195f, -0.975f, -0.585f, -1.365f)
            moveTo(13.475f, 20.3f)
            lineTo(4.7f, 11.525f)
            verticalLineToRelative(-6.825f)
            horizontalLineToRelative(6.825f)
            lineToRelative(8.775f, 8.775f)
            moveTo(7.137f, 5.675f)
            curveToRelative(0.78f, 0f, 1.462f, 0.682f, 1.462f, 1.462f)
            reflectiveCurveToRelative(-0.682f, 1.462f, -1.462f, 1.462f)
            reflectiveCurveToRelative(-1.462f, -0.682f, -1.462f, -1.462f)
            reflectiveCurveToRelative(0.682f, -1.462f, 1.462f, -1.462f)
            moveTo(10.648f, 9.477f)
            lineToRelative(1.365f, -1.365f)
            lineToRelative(5.363f, 5.363f)
            lineToRelative(-1.365f, 1.365f)
            lineToRelative(-5.363f, -5.363f)
            moveTo(8.21f, 11.915f)
            lineToRelative(1.365f, -1.365f)
            lineToRelative(3.9f, 3.9f)
            lineToRelative(-1.365f, 1.365f)
            lineToRelative(-3.9f, -3.9f)
            close()
        }
    }.build()
}

val Icons.TwoTone.TagText: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoToneTagText",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.665f, 12.11f)
            lineTo(12.89f, 3.335f)
            curveToRelative(-0.39f, -0.39f, -0.878f, -0.585f, -1.365f, -0.585f)
            horizontalLineToRelative(-6.825f)
            curveToRelative(-1.073f, 0f, -1.95f, 0.878f, -1.95f, 1.95f)
            verticalLineToRelative(6.825f)
            curveToRelative(0f, 0.487f, 0.195f, 0.975f, 0.585f, 1.365f)
            lineToRelative(8.775f, 8.775f)
            curveToRelative(0.39f, 0.39f, 0.878f, 0.585f, 1.365f, 0.585f)
            curveToRelative(0.488f, 0f, 0.975f, -0.195f, 1.365f, -0.585f)
            lineToRelative(6.825f, -6.825f)
            curveToRelative(0.39f, -0.39f, 0.585f, -0.878f, 0.585f, -1.365f)
            curveToRelative(0f, -0.487f, -0.195f, -0.975f, -0.585f, -1.365f)
            close()
            moveTo(13.475f, 20.3f)
            lineTo(4.7f, 11.525f)
            verticalLineToRelative(-6.825f)
            horizontalLineToRelative(6.825f)
            lineToRelative(8.775f, 8.775f)
            lineToRelative(-6.825f, 6.825f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(7.137f, 5.675f)
            curveToRelative(0.78f, 0f, 1.462f, 0.682f, 1.462f, 1.462f)
            reflectiveCurveToRelative(-0.682f, 1.462f, -1.462f, 1.462f)
            reflectiveCurveToRelative(-1.462f, -0.682f, -1.462f, -1.462f)
            reflectiveCurveToRelative(0.682f, -1.462f, 1.462f, -1.462f)
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(10.648f, 9.477f)
            lineToRelative(1.365f, -1.365f)
            lineToRelative(5.363f, 5.363f)
            lineToRelative(-1.365f, 1.365f)
            lineToRelative(-5.363f, -5.362f)
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(8.21f, 11.915f)
            lineToRelative(1.365f, -1.365f)
            lineToRelative(3.9f, 3.9f)
            lineToRelative(-1.365f, 1.365f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(13.475f, 20.3f)
            lineToRelative(-8.775f, -8.775f)
            lineToRelative(0f, -6.825f)
            lineToRelative(6.825f, 0f)
            lineToRelative(8.775f, 8.775f)
        }
    }.build()
}