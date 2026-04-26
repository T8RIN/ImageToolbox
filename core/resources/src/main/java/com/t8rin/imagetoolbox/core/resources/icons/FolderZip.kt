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

val Icons.Outlined.FolderZip: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FolderZip",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(640f, 480f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(640f, 560f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(80f)
            close()
            moveTo(640f, 640f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(447f, 320f)
            lineToRelative(-80f, -80f)
            lineTo(160f, 240f)
            verticalLineToRelative(480f)
            horizontalLineToRelative(400f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(160f)
            verticalLineToRelative(-400f)
            lineTo(640f, 320f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-80f)
            lineTo(447f, 320f)
            close()
            moveTo(160f, 800f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 720f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 160f)
            horizontalLineToRelative(207f)
            quadToRelative(16f, 0f, 30.5f, 6f)
            reflectiveQuadToRelative(25.5f, 17f)
            lineToRelative(57f, 57f)
            horizontalLineToRelative(320f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 320f)
            verticalLineToRelative(400f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 800f)
            lineTo(160f, 800f)
            close()
            moveTo(160f, 720f)
            verticalLineToRelative(-480f)
            verticalLineToRelative(480f)
            close()
        }
    }.build()
}

val Icons.TwoTone.FolderZip: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.FolderZip",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(16f, 12f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            close()
            moveTo(16f, 14f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            close()
            moveTo(16f, 16f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            close()
            moveTo(11.175f, 8f)
            lineToRelative(-2f, -2f)
            horizontalLineToRelative(-5.175f)
            verticalLineToRelative(12f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(4f)
            verticalLineTo(8f)
            horizontalLineToRelative(-4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-2.825f)
            close()
            moveTo(4f, 20f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(6f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(5.175f)
            curveToRelative(0.267f, 0f, 0.521f, 0.05f, 0.762f, 0.15f)
            reflectiveCurveToRelative(0.454f, 0.242f, 0.637f, 0.425f)
            lineToRelative(1.425f, 1.425f)
            horizontalLineToRelative(8f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(10f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineTo(4f)
            close()
            moveTo(4f, 18f)
            verticalLineTo(6f)
            verticalLineToRelative(12f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(11.175f, 8f)
            lineToRelative(-2f, -2f)
            lineToRelative(-5.175f, 0f)
            lineToRelative(0f, 12f)
            lineToRelative(10f, 0f)
            lineToRelative(0f, -2f)
            lineToRelative(2f, 0f)
            lineToRelative(0f, 2f)
            lineToRelative(4f, 0f)
            lineToRelative(0f, -10f)
            lineToRelative(-4f, 0f)
            lineToRelative(0f, 2f)
            lineToRelative(-2f, 0f)
            lineToRelative(0f, -2f)
            lineToRelative(-2.825f, 0f)
            close()
        }
    }.build()
}