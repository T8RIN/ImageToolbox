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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.WebpBox: ImageVector by lazy {
    ImageVector.Builder(
        name = "WebpBox",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6.757f, 13.855f)
            curveToRelative(-0.409f, 0f, -0.741f, -0.332f, -0.741f, -0.741f)
            verticalLineToRelative(-2.965f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(2.965f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(-2.594f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(2.594f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(-2.965f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(2.965f)
            curveToRelative(0f, 0.409f, -0.332f, 0.741f, -0.741f, 0.741f)
            horizontalLineTo(6.757f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(10.252f, 10.145f)
            verticalLineToRelative(3.706f)
            horizontalLineToRelative(2.224f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(-1.483f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(1.483f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(-1.483f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(1.483f)
            verticalLineToRelative(-0.741f)
            horizontalLineTo(10.252f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(15.23f, 11.446f)
            verticalLineToRelative(-0.556f)
            curveToRelative(0f, -0.409f, -0.332f, -0.741f, -0.741f, -0.741f)
            horizontalLineToRelative(-1.483f)
            verticalLineToRelative(3.706f)
            horizontalLineToRelative(1.483f)
            curveToRelative(0.409f, 0f, 0.741f, -0.332f, 0.741f, -0.741f)
            verticalLineToRelative(-0.556f)
            curveToRelative(0f, -0.297f, -0.259f, -0.556f, -0.556f, -0.556f)
            curveTo(14.971f, 12.002f, 15.23f, 11.742f, 15.23f, 11.446f)
            moveTo(14.489f, 13.114f)
            horizontalLineToRelative(-0.741f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(0.741f)
            verticalLineTo(13.114f)
            moveTo(14.489f, 11.631f)
            horizontalLineToRelative(-0.741f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(0.741f)
            verticalLineTo(11.631f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(15.761f, 10.148f)
            verticalLineToRelative(3.706f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(-1.483f)
            horizontalLineToRelative(0.741f)
            curveToRelative(0.409f, 0f, 0.741f, -0.332f, 0.741f, -0.741f)
            verticalLineToRelative(-0.741f)
            curveToRelative(0f, -0.409f, -0.332f, -0.741f, -0.741f, -0.741f)
            horizontalLineTo(15.761f)
            moveTo(16.502f, 10.89f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(0.741f)
            horizontalLineToRelative(-0.741f)
            verticalLineTo(10.89f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(19f, 3f)
            horizontalLineTo(5f)
            curveTo(3.89f, 3f, 3f, 3.89f, 3f, 5f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.105f, 0.895f, 2f, 2f, 2f)
            horizontalLineToRelative(14f)
            curveToRelative(1.105f, 0f, 2f, -0.895f, 2f, -2f)
            verticalLineTo(5f)
            curveTo(21f, 3.89f, 20.1f, 3f, 19f, 3f)
            moveTo(19f, 5f)
            verticalLineToRelative(14f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineTo(19f)
            close()
        }
    }.build()
}

val Icons.TwoTone.WebpBox: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.WebpBox",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5f, 5f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(-14f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6.757f, 13.855f)
            curveToRelative(-0.409f, 0f, -0.741f, -0.332f, -0.741f, -0.741f)
            verticalLineToRelative(-2.965f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(2.965f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(-2.594f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(2.594f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(-2.965f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(2.965f)
            curveToRelative(0f, 0.409f, -0.332f, 0.741f, -0.741f, 0.741f)
            horizontalLineToRelative(-2.223f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(10.252f, 10.145f)
            verticalLineToRelative(3.706f)
            horizontalLineToRelative(2.224f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(-1.483f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(1.483f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(-1.483f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(1.483f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(-2.224f)
            verticalLineToRelative(-0.001f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(15.23f, 11.446f)
            verticalLineToRelative(-0.556f)
            curveToRelative(0f, -0.409f, -0.332f, -0.741f, -0.741f, -0.741f)
            horizontalLineToRelative(-1.483f)
            verticalLineToRelative(3.706f)
            horizontalLineToRelative(1.483f)
            curveToRelative(0.409f, 0f, 0.741f, -0.332f, 0.741f, -0.741f)
            verticalLineToRelative(-0.556f)
            curveToRelative(0f, -0.297f, -0.259f, -0.556f, -0.556f, -0.556f)
            curveToRelative(0.297f, -0f, 0.556f, -0.26f, 0.556f, -0.556f)
            moveTo(14.489f, 13.114f)
            horizontalLineToRelative(-0.741f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(0.741f)
            moveTo(14.489f, 11.631f)
            horizontalLineToRelative(-0.741f)
            verticalLineToRelative(-0.741f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(0.741f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(15.761f, 10.148f)
            verticalLineToRelative(3.706f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(-1.483f)
            horizontalLineToRelative(0.741f)
            curveToRelative(0.409f, 0f, 0.741f, -0.332f, 0.741f, -0.741f)
            verticalLineToRelative(-0.741f)
            curveToRelative(0f, -0.409f, -0.332f, -0.741f, -0.741f, -0.741f)
            horizontalLineToRelative(-1.482f)
            moveTo(16.502f, 10.89f)
            horizontalLineToRelative(0.741f)
            verticalLineToRelative(0.741f)
            horizontalLineToRelative(-0.741f)
            verticalLineToRelative(-0.741f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(19f, 3f)
            horizontalLineTo(5f)
            curveToRelative(-1.11f, 0f, -2f, 0.89f, -2f, 2f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.105f, 0.895f, 2f, 2f, 2f)
            horizontalLineToRelative(14f)
            curveToRelative(1.105f, 0f, 2f, -0.895f, 2f, -2f)
            verticalLineTo(5f)
            curveToRelative(0f, -1.11f, -0.9f, -2f, -2f, -2f)
            moveTo(19f, 5f)
            verticalLineToRelative(14f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(14f)
            close()
        }
    }.build()
}